package protocol3.backend;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;

public class ItemCheck
{

	// Banned materials.
	public static Material[] Banned = { Material.BEDROCK, Material.BARRIER, Material.COMMAND_BLOCK,
			Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
			Material.END_PORTAL_FRAME, Material.SPAWNER, Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK };

	// Items that need to be specially rebuilt.
	public static Material[] Special = { Material.ENCHANTED_BOOK, Material.POTION, Material.LINGERING_POTION,
			Material.TIPPED_ARROW, Material.SPLASH_POTION, Material.WRITTEN_BOOK, Material.FILLED_MAP,
			Material.PLAYER_WALL_HEAD, Material.PLAYER_HEAD, Material.WRITABLE_BOOK, Material.BEEHIVE,
			Material.BEE_NEST, Material.RESPAWN_ANCHOR, Material.FIREWORK_ROCKET, Material.FIREWORK_STAR,
			Material.SHIELD };

	public static Material[] LegalHeads = { Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL,
			Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD };

	public static void IllegalCheck(ItemStack item)
	{
		if (Config.getValue("item.illegal").equals("0"))
			return;

		// Dont check null items
		if (item == null)
			return;

		// Dont check air
		if (item.getType().equals(Material.AIR))
			return;

		// Iterate through shulker boxes

		if (item.getItemMeta() instanceof BlockStateMeta)
		{
			BlockStateMeta im = (BlockStateMeta) item.getItemMeta();
			if (im.getBlockState() instanceof ShulkerBox)
			{
				ShulkerBox shulker = (ShulkerBox) im.getBlockState();
				for (ItemStack i : shulker.getInventory().getStorageContents())
				{
					if (isShulker(i))
					{
						i.setAmount(0);
						shulker.update();
						continue;
					}
					IllegalCheck(i);
					shulker.update();
				}
				if (item.getAmount() > 1)
					item.setAmount(1);
				shulker.update();
				return;
			}
		}

		// Delete banned items

		for (Material mat : Banned)
		{
			if (item.getType().equals(mat))
			{
				item.setAmount(0);
				return;
			}
		}

		// Determine if item needs to be specially rebuilt
		boolean specialRebuild = false;
		for (Material s : Special)
		{
			if (item.getType().equals(s))
			{
				specialRebuild = true;
			}
		}

		// Delete spawn eggs

		if (item.getType().toString().toUpperCase().contains("SPAWN") && !specialRebuild)
		{
			item.setAmount(0);
			return;
		}

		// Patch stacked items
		if (item.getAmount() > item.getMaxStackSize() && Config.getValue("item.illegal.stacked").equals("0")
				&& !item.getType().equals(Material.SNOWBALL))
		{
			item.setAmount(item.getMaxStackSize());
		}

		// Reset item meta

		if (item.hasItemMeta() && !specialRebuild && !(item.getItemMeta() instanceof BannerMeta))
		{

			ItemMeta newMeta = Bukkit.getItemFactory().getItemMeta(item.getType());

			// Rebuild Basic Item Attribs

			if (item.getItemMeta().hasDisplayName())
				newMeta.setDisplayName(item.getItemMeta().getDisplayName());
			if (item.getItemMeta().hasLore())
				newMeta.setLore(item.getItemMeta().getLore());

			// Rebuild Item Enchants

			if (item.getItemMeta().hasEnchants())
			{
				try
				{
					for (Enchantment e : item.getEnchantments().keySet())
					{

						// If this item does not support this enchantment
						if (!e.canEnchantItem(item) && Config.getValue("item.illegal.invalid").equals("0"))
							continue;

						if (Config.getValue("item.illegal.invalid").equals("0"))
						{
							// If this item has a conflict with another enchantment on the same item
							boolean hasConflict = false;

							if (newMeta.getEnchants() != null)
							{
								for (Enchantment etwo : newMeta.getEnchants().keySet())
								{
									// Ignore self
									if (etwo.equals(e))
										continue;

									// Remove conflicts
									if (etwo.conflictsWith(e))
									{
										hasConflict = true;
									}

									// Except Infinity + Mending
									if ((etwo.equals(Enchantment.ARROW_INFINITE) && e.equals(Enchantment.MENDING)))
									{
										hasConflict = false;
									} else if ((etwo.equals(Enchantment.MENDING)
											&& e.equals(Enchantment.ARROW_INFINITE)))
									{
										hasConflict = false;
									}
								}
							}

							if (hasConflict)
								continue;
						}

						if (item.getEnchantmentLevel(e) > e.getMaxLevel())
						{
							newMeta.addEnchant(e, e.getMaxLevel(), false);
						} else
						{
							newMeta.addEnchant(e, item.getEnchantmentLevel(e), false);
						}
					}
				} catch (IllegalArgumentException e)
				{
					item.setAmount(0);
					return;
				}
			}

			// Rebuild Item Durability

			if (newMeta instanceof Damageable)
			{
				Damageable dmg = (Damageable) newMeta;
				Damageable oldDmg = (Damageable) item.getItemMeta();
				dmg.setDamage(oldDmg.getDamage());
				newMeta = (ItemMeta) dmg;
				if (Config.getValue("item.illegal.unbreakable").equals("0"))
				{
					newMeta.setUnbreakable(item.getItemMeta().isUnbreakable());
				} else
				{
					newMeta.setUnbreakable(false);
				}
			}

			// Set item to rebuilt item

			item.setItemMeta(newMeta);
		}

		// Rebuild enchanted books

		if (item.getType().equals(Material.ENCHANTED_BOOK))
		{

			EnchantmentStorageMeta newMeta = (EnchantmentStorageMeta) Bukkit.getItemFactory()
					.getItemMeta(Material.ENCHANTED_BOOK);

			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

			if (meta.getStoredEnchants() == null)
			{
				item.setAmount(0);
				return;
			}

			// Rebuild stored enchants

			for (Enchantment e : meta.getStoredEnchants().keySet())
			{
				if (meta.getStoredEnchantLevel(e) > e.getMaxLevel())
				{
					newMeta.addStoredEnchant(e, e.getMaxLevel(), false);
					continue;
				}
				newMeta.addStoredEnchant(e, meta.getStoredEnchantLevel(e), false);
			}

			if (meta.hasDisplayName())
				newMeta.setDisplayName(meta.getDisplayName());
			if (meta.hasLore())
				newMeta.setLore(meta.getLore());

			item.setItemMeta(newMeta);
			return;
		}

		// Fix potions

		if (item.getType().equals(Material.POTION) || item.getType().equals(Material.SPLASH_POTION)
				|| item.getType().equals(Material.TIPPED_ARROW) || item.getType().equals(Material.LINGERING_POTION))
		{

			PotionMeta meta = (PotionMeta) item.getItemMeta();

			// Remove uncraftable potions or those with custom effects

			if (meta.getBasePotionData().getType().equals(PotionType.UNCRAFTABLE))
			{
				item.setAmount(0);
				return;
			}
			meta.clearCustomEffects();

			item.setItemMeta(meta);

			removeEnchants(item);
			return;
		}

		// Fix written books

		if (item.getType().equals(Material.WRITTEN_BOOK) || item.getType().equals(Material.WRITABLE_BOOK))
		{
			ItemMeta meta = item.getItemMeta();
			if (meta.hasEnchants())
			{
				for (Enchantment ench : meta.getEnchants().keySet())
				{
					meta.removeEnchant(ench);
				}
			}
			item.setItemMeta(meta);
			BookMeta bm = (BookMeta) item.getItemMeta();
			if (bm.getPageCount() > 5)
			{
				for (int x = 5; x < bm.getPageCount(); x++)
				{
					bm.setPage(x, "");
				}
			}
			if (item.getAmount() > 16)
			{
				item.setAmount(16);
			}
			item.setItemMeta(bm);
			return;
		}

		// Fix maps
		if (item.getType().equals(Material.FILLED_MAP))
		{
			removeEnchants(item);
			return;
		}

		// Fix banners
		if (item.getItemMeta() instanceof BannerMeta)
		{
			removeEnchants(item);
			return;
		}

		// Fix respawn anchors (?)
		if (item.getType().equals(Material.RESPAWN_ANCHOR))
		{
			removeEnchants(item);
			return;
		}

		// Fix beehives
		if (item.getType().equals(Material.BEEHIVE))
		{
			removeEnchants(item);
			return;
		}

		// Fix beenests
		if (item.getType().equals(Material.BEE_NEST))
		{
			removeEnchants(item);
			return;
		}

		// Fix fireworks and firework stars
		if (item.getType().equals(Material.FIREWORK_ROCKET) || item.getType().equals(Material.FIREWORK_STAR))
		{
			removeEnchants(item);
			return;
		}

		// Fix shields
		if (item.getType().equals(Material.SHIELD))
		{
			removeEnchants(item);
			return;
		}

		// Delete player heads (exempt wither heads)
		if (item.getItemMeta() instanceof SkullMeta && Config.getValue("item.illegal.heads").equals("0"))
		{
			for (Material m : LegalHeads)
			{
				if (item.getType().equals(m))
				{
					return;
				}
			}
			item.setAmount(0);
			return;
		}

		// Fix player heads
		else if (item.getItemMeta() instanceof SkullMeta && Config.getValue("item.illegal.heads").equals("1"))
		{
			removeEnchants(item);
			return;
		}
	}

	// Remove item enchants.
	private static void removeEnchants(ItemStack item)
	{
		if (item == null)
			return;
		ItemMeta meta = item.getItemMeta();
		if (meta.hasEnchants())
		{
			for (Enchantment ench : meta.getEnchants().keySet())
			{
				if (!ench.canEnchantItem(item))
				{
					meta.removeEnchant(ench);
					continue;
				}
				if (ench.getMaxLevel() > meta.getEnchantLevel(ench))
				{
					meta.removeEnchant(ench);
					continue;
				}
			}
		}
		item.setItemMeta(meta);
	}

	// Determine if item is shulker.
	private static boolean isShulker(ItemStack i)
	{
		if (i == null)
			return false;
		if (!i.hasItemMeta())
			return false;
		if (i.getItemMeta() instanceof BlockStateMeta)
		{
			BlockStateMeta im = (BlockStateMeta) i.getItemMeta();
			if (im.getBlockState() instanceof ShulkerBox)
			{
				if (i.getAmount() > 1)
					i.setAmount(1);
				return true;
			}
		}
		return false;
	}

}