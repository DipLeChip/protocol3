package protocol3.events;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;
import protocol3.commands.Admin;

// Chat Events
// protocol3. ~~DO NOT REDISTRIBUTE!~~ n/a 3/6/2021

public class Chat implements Listener
{

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		// Cancel this event so we can override vanilla chat
		e.setCancelled(true);

		// Don't execute period if the player is muted
		if (PlayerMeta.isMuted(e.getPlayer()) || (PlayerMeta.MuteAll && !e.getPlayer().isOp()))
			return;

		// -- CREATE PROPERTIES --

		// Chat color to send the message with.
		String color;
		// The final edited message.
		String finalMessage = e.getMessage();
		// If we should send the final message.
		boolean doSend = true;
		// The username color.
		String usernameColor;

		// -- SET CHAT COLORS -- //

		// Greentext
		if (e.getMessage().startsWith(">"))
		{
			color = "§a";
		}
		// Donator text
		else if (e.getMessage().startsWith("$") && PlayerMeta.isDonator(e.getPlayer()))
		{
			color = "§6";
		}
		// Normal text
		else
		{
			color = "§f";
		}

		if (PlayerMeta.isDonator(e.getPlayer()) && !Admin.UseRedName.contains(e.getPlayer().getUniqueId()))
		{
			usernameColor = "§6";
		} else if (Admin.UseRedName.contains(e.getPlayer().getUniqueId()))
		{
			usernameColor = "§c";
		} else
		{
			usernameColor = "§f";
		}

		// -- STRING MODIFICATION -- //

		// Remove section symbols
		finalMessage = finalMessage.replace('§', ' ');

		// -- CHECKS -- //

		if (isBlank(finalMessage))
			doSend = false;

		if (PlayerMeta.isLagfag(e.getPlayer()))
		{
			finalMessage = finalMessage + "; i am a registered lagfag";
		}

		// -- SEND FINAL MESSAGE -- //

		if (doSend)
		{
			String username = e.getPlayer().getName();

			TextComponent finalCom = new TextComponent(
					"§f<" + usernameColor + username + "§f> " + color + finalMessage);

			Bukkit.getServer().spigot().broadcast(finalCom);

			Bukkit.getLogger().log(Level.INFO, "§f<" + usernameColor + username + "§f> " + color + finalMessage);
		}
	}

	@EventHandler
	public boolean onCommand(PlayerCommandPreprocessEvent e)
	{
		if (e.getMessage().split(" ")[0].contains(":") && !e.getPlayer().isOp())
		{
			e.getPlayer().spigot().sendMessage(new TextComponent("§cUnknown command."));
			e.setCancelled(true);
			return true;
		}
		return true;
	}

	// String blank check since HeavyNode is on an older version of Java
	// that doesn't support this
	private boolean isBlank(String check)
	{
		// If string length is 0, it is empty
		boolean isEmpty = check.length() == 0;
		if (isEmpty)
			return true;

		// Return true if there is any character in the array
		char[] checkArray = new char[check.length()];
		for (int i = 0; i < check.length(); i++)
		{
			checkArray[i] = check.charAt(i);
		}

		// If it's a space, count it as an empty character
		for (char c : checkArray)
		{
			if (c == ' ')
				continue;
			else
				return false;
		}
		return true;
	}
}
