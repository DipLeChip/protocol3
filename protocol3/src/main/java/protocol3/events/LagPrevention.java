package protocol3.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import protocol3.backend.Config;

public class LagPrevention implements Listener
{
	public static int currentWithers = 0;

	public void onEntitySpawn(EntitySpawnEvent e)
	{
		int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));
		if (e.getEntityType().equals(EntityType.WITHER))
		{
			if (e.getEntity().getTicksLived() > 200)
			{
				return;
			}
			if (currentWithers + 1 > witherLimit)
			{
				e.setCancelled(true);
				return;
			}
			currentWithers = getWithers();
		}
	}

	public static int getWithers()
	{
		int toRet = 0;
		int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));
		for (Entity e : Bukkit.getWorld("world").getEntities())
		{
			if (e.getType().equals(EntityType.WITHER))
			{
				toRet++;
				if (toRet > witherLimit)
				{
					toRet--;
					Wither w = (Wither) e;
					w.setHealth(0);
				}
			}
		}
		for (Entity e : Bukkit.getWorld("world_nether").getEntities())
		{
			if (e.getType().equals(EntityType.WITHER))
			{
				toRet++;
				if (toRet > witherLimit)
				{
					toRet--;
					Wither w = (Wither) e;
					w.setHealth(0);
				}
			}
		}
		for (Entity e : Bukkit.getWorld("world_the_end").getEntities())
		{
			if (e.getType().equals(EntityType.WITHER))
			{
				toRet++;
				if (toRet > witherLimit)
				{
					toRet--;
					Wither w = (Wither) e;
					w.setHealth(0);
				}
			}
		}
		return toRet;
	}

}
