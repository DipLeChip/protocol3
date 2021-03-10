package protocol3.commands;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.Main;
import protocol3.backend.LagProcessor;
import protocol3.backend.PlayerMeta;
import protocol3.backend.ServerMeta;
import protocol3.backend.Utilities;
import protocol3.events.LagPrevention;
import protocol3.tasks.ProcessPlaytime;

public class Server implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		sender.spigot().sendMessage(new TextComponent("§c========== GENERAL =========="));
		sender.spigot().sendMessage(
				new TextComponent("§cServer Uptime:§7 " + Utilities.calculateTime(ServerMeta.getUptime())));
		sender.spigot().sendMessage(new TextComponent("§cCurrent Population:§7 " + Bukkit.getOnlinePlayers().size()));
		sender.spigot().sendMessage(
				new TextComponent("§cCurrent TPS:§7 " + new DecimalFormat("#.##").format(LagProcessor.getTPS())));
		sender.spigot().sendMessage(new TextComponent(
				"§cCurrent Speed Limit:§7 " + (LagProcessor.getTPS() <= 15 ? "48" : "64") + " blocks per second"));
		sender.spigot().sendMessage(
				new TextComponent("§cAnti-Cheat Enabled: §7" + (LagProcessor.getTPS() <= 10 ? "True" : "False")));
		sender.spigot().sendMessage(new TextComponent("§c========== PLAYER =========="));
		sender.spigot().sendMessage(
				new TextComponent("§cUnique Joins (§eSince Map Creation§c):§7 " + Bukkit.getOfflinePlayers().length));
		sender.spigot().sendMessage(new TextComponent(
				"§cUnique Joins (§eSince Stats Update§c):§7 " + PlayerMeta.Playtimes.keySet().size()));
		sender.spigot().sendMessage(new TextComponent("§cDonators:§7 " + PlayerMeta._donatorList.size()));
		sender.spigot().sendMessage(new TextComponent("§cLagfags:§7 " + PlayerMeta._lagfagList.size()));
		sender.spigot().sendMessage(new TextComponent("§cPermanent Mutes:§7 " + PlayerMeta._permanentMutes.size()));
		sender.spigot().sendMessage(new TextComponent("§cOP Accounts:§7 " + Bukkit.getOperators().size()));
		sender.spigot().sendMessage(new TextComponent("§c=========== DEBUG ==========="));
		sender.spigot().sendMessage(
				new TextComponent("§cServer Restarting: §7" + (Main.alreadyRestarting ? "True" : "False")));
		sender.spigot().sendMessage(new TextComponent("§cTime below acceptable TPS:§7 " + ProcessPlaytime.lowTpsCounter
				+ "ms (600000ms required to restart)"));
		sender.spigot().sendMessage(new TextComponent("§cWither Count:§7 " + LagPrevention.currentWithers));
		return true;

	}
}