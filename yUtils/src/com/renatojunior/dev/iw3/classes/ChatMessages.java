package com.renatojunior.dev.iw3.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ChatMessages {
	private final ConsoleCommandSender console;

	public ChatMessages() {
		this.console = Bukkit.getServer().getConsoleSender();
	}

	public boolean sendMessage(CommandSender sender, String message) {
		if ((sender instanceof Player)) {
			Player player = (Player) sender;
			player.sendMessage(getMessageColor(message));
			return false;
		}
		this.console.sendMessage(getMessageColor(message));
		return false;
	}

	public boolean consoleMessage(String message) {
		this.console.sendMessage(getMessageColor(message));
		return false;
	}

	@SuppressWarnings("static-method")
	public String getMessageColor(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
