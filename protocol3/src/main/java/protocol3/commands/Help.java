package protocol3.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;

public class Help implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		try
		{
			displayPage(Integer.parseInt(args[0]), sender);
		} catch (Exception ex)
		{
			displayPage(1, sender);
		}
		return true;
	}

	private void displayPage(int page, CommandSender sender)
	{
		int maxPage = 2;
		if (page > maxPage)
		{
			page = maxPage;
		} else if (page < 1)
		{
			page = 1;
		}

		sender.spigot().sendMessage(new TextComponent("§6--- HELP: " + page + " ---"));
		if (page == 1)
		{
			sender.spigot().sendMessage(new TextComponent("§6/help: §7This list of commands"));
			sender.spigot().sendMessage(new TextComponent(
					"§6/stats [playername/top/leaderboard]: §7Get a list of statistics about a player"));
			sender.spigot().sendMessage(new TextComponent("§6/discord: §7Join the discord."));
			sender.spigot()
					.sendMessage(new TextComponent("§6/kit: §7Get a netherite kit with steak and more useful tools."));
			sender.spigot()
					.sendMessage(new TextComponent("§6/vote: §7Dupe the item in your hand. Only occurs after voting."));
			sender.spigot().sendMessage(
					new TextComponent("§6/sign: §7Sign the item you are holding. Cannot undo or overwrite."));
		} else if (page == 2)
		{
			sender.spigot().sendMessage(new TextComponent("§6/server: §7Get statistics about the server."));
			sender.spigot().sendMessage(new TextComponent("§6/vm [player]: §7Vote to mute a player."));
			sender.spigot().sendMessage(new TextComponent("§6/kill, /suicide, /kys: §7End it all."));
			sender.spigot().sendMessage(new TextComponent("§6/msg, /w, /r: §7Message or reply to a player."));
			sender.spigot().sendMessage(new TextComponent("§6/tdm: §7Toggle death messages."));
			sender.spigot().sendMessage(new TextComponent("§6/tjm: §7Toggle join messages."));
		}
		if (page + 1 >= maxPage)
		{
			sender.spigot().sendMessage(new TextComponent("§6--- /HELP " + (page + 1) + " FOR NEXT PAGE ---"));
		} else
		{
			sender.spigot().sendMessage(new TextComponent("§6--- END OF HELP ---"));
		}
	}
}
