package protocol3.tasks;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.TimerTask;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.LagProcessor;
import protocol3.backend.Scheduler;

//Auto announcer
public class AutoAnnouncer extends TimerTask
{
	Random r = new Random();

	@Override
	public void run()
	{
		int rnd = r.nextInt(10);

		if (rnd == 0)
		{
			String tps = new DecimalFormat("#.##").format(LagProcessor.getTPS());
			Bukkit.spigot().broadcast(new TextComponent("§6You are playing on AVAS Survival. TPS is " + tps + "."));
		} else if (rnd == 1)
		{
			Bukkit.spigot()
					.broadcast(new TextComponent("§6You can vote to mute a player by doing §l/vm [playername]."));
		} else if (rnd == 2)
		{
			Bukkit.spigot().broadcast(new TextComponent(
					"§6You can dupe the item in your hand by holding the item you want to dupe and then voting using /vote. (not a troll)"));
		} else if (rnd == 3)
		{
			Bukkit.spigot().broadcast(new TextComponent(
					"§6Dont forget to get your /kit starter, including §lsteak§6, basic §ldiamond armor§6, and a §lnetherite sword."));
		} else if (rnd == 4)
		{
			Bukkit.spigot().broadcast(
					new TextComponent("§6You can sign items to show them as uniquely yours by doing §l/sign."));
		} else if (rnd == 5)
		{
			Bukkit.spigot()
					.broadcast(new TextComponent("§6You can buy donor for life for $20 at https://avas.cc/donate"));
		} else if (rnd == 6)
		{
			Bukkit.spigot()
					.broadcast(new TextComponent("§6You can buy an MOTD for life for $10 at https://avas.cc/donate"));
		} else if (rnd == 7)
		{
			Bukkit.spigot().broadcast(
					new TextComponent("§6You can toggle death messages with /tdm, and join messages with /tjm."));
		} else if (rnd == 8)
		{
			Bukkit.spigot().broadcast(new TextComponent("§6Lagging the server will result in §lsevere consequences."));
		} else if (rnd == 9)
		{
			Bukkit.spigot().broadcast(new TextComponent("§6Do /help to see the commands available to you."));
		}
		Scheduler.setLastTaskId("autoAnnounce");
	}
}