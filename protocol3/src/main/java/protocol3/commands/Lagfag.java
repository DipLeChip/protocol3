package protocol3.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;

// funny command haha

public class Lagfag implements CommandExecutor
{

	HashMap<UUID, Boolean> threadIndicators = new HashMap<UUID, Boolean>();
	HashMap<UUID, Boolean> threadProgression = new HashMap<UUID, Boolean>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof ConsoleCommandSender) && !sender.isOp())
		{
			sender.spigot().sendMessage(new TextComponent("§cYou can't run this."));
			return true;
		}
		if (args.length != 1)
		{
			sender.spigot().sendMessage(new TextComponent("§cInvalid syntax. Syntax: /lagfag [name]"));
			return true;
		}

		if (args[0].equals("cam") && sender.isOp())
		{
			Bukkit.getScheduler().runTaskAsynchronously(protocol3.Main.instance, new Runnable()
			{
				@Override
				public void run()
				{
					while (true)
					{
						Player op = (Player) sender;
						Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
						for (Player p : players)
						{
							if (p.isOp())
								continue;
							Bukkit.getScheduler().runTask(protocol3.Main.instance, new Runnable()
							{
								@Override
								public void run()
								{
									op.setGameMode(GameMode.SPECTATOR);
									op.teleport(p.getLocation());
								}
							});
							op.sendMessage("§6Player: " + p.getName());

							while (!threadProgression.get(op.getUniqueId()) && !threadIndicators.get(op.getUniqueId()))
							{
								try
								{
									Thread.sleep(500);
								} catch (InterruptedException e)
								{
								}
							}

							if (threadIndicators.get(op.getUniqueId()))
							{
								threadIndicators.remove(op.getUniqueId());
								threadProgression.remove(op.getUniqueId());
								break;
							}

							if (threadProgression.get(op.getUniqueId()))
							{
								threadProgression.put(op.getUniqueId(), false);
							}

						}
						if (threadIndicators.get(op.getUniqueId()))
						{
							threadIndicators.remove(op.getUniqueId());
							threadProgression.remove(op.getUniqueId());
							break;
						}
					}
					return;
				}
			});
			Player oper = (Player) sender;
			threadIndicators.put(oper.getUniqueId(), false);
			threadProgression.put(oper.getUniqueId(), false);
			return true;
		}

		if (args[0].equals("cancel") && sender.isOp())
		{
			Player op = (Player) sender;
			if (threadIndicators.containsKey(op.getUniqueId()))
			{
				threadIndicators.put(op.getUniqueId(), true);
			}
			return true;
		}

		if (args[0].equals("next") && sender.isOp())
		{
			Player op = (Player) sender;
			if (threadProgression.containsKey(op.getUniqueId()))
			{
				threadProgression.put(op.getUniqueId(), true);
			}
			return true;
		}

		Player lagfag = Bukkit.getPlayer(args[0]);
		if (lagfag == null)
		{
			sender.spigot().sendMessage(new TextComponent("§cPlayer is not online."));
			return true;
		}
		PlayerMeta.setLagfag(lagfag, !PlayerMeta.isLagfag(lagfag));
		if (PlayerMeta.isLagfag(lagfag))
		{
			Bukkit.getServer().spigot().broadcast(new TextComponent("§6" + lagfag.getName() + " is a lagfag!"));

			Bukkit.getServer().spigot().broadcast(
					new TextComponent("§6IP: " + lagfag.getAddress().toString().split(":")[0].replace("/", "")));
			Bukkit.getServer().spigot()
					.broadcast(new TextComponent("§6COORDS: " + Math.round(lagfag.getLocation().getX()) + ", "
							+ Math.round(lagfag.getLocation().getY()) + ", "
							+ Math.round(lagfag.getLocation().getZ())));
			lagfag.getEnderChest().clear();
			lagfag.setBedSpawnLocation(Bukkit.getWorld("world").getSpawnLocation(), true);
			lagfag.setHealth(0);
		}
		return true;
	}

}
