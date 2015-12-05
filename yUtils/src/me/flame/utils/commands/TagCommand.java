package me.flame.utils.commands;

import me.flame.utils.Main;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.tagmanager.TagManager;
import me.flame.utils.tagmanager.enums.Tag;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TagCommand implements CommandExecutor {
	private PermissionManager manager;
	private TagManager tagManager;

	public TagCommand(Main main) {
		this.manager = main.getPermissionManager();
		this.tagManager = main.getTagManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		String message = "Default | Normal";
		if (manager.getPlugin().getTorneioManager().isParticipante(p.getUniqueId()))
			message = message + " | TORNEIO";
		if (manager.hasGroupPermission(p, Group.LIGHT))
			message = message + " | LIGHT";
		if (manager.hasGroupPermission(p, Group.PREMIUM))
			message = message + " | PREMIUM";
		if (manager.hasGroupPermission(p, Group.ULTIMATE))
			message = message + " | ULTIMATE";
		if (manager.hasGroupPermission(p, Group.YOUTUBER))
			message = message + " | YOUTUBER";
		if (manager.hasGroupPermission(p, Group.DEV))
			message = message + " | DEV";
		if (manager.hasGroupPermission(p, Group.BUILDER))
			message = message + " | BUILDER";
		if (manager.hasGroupPermission(p, Group.HELPER))
			message = message + " | HELPER";
		if (manager.hasGroupPermission(p, Group.STAFF))
			message = message + " | STAFF";
		if (manager.hasGroupPermission(p, Group.TRIAL))
			message = message + " | TRIAL";
		if (manager.hasGroupPermission(p, Group.MOD))
			message = message + " | MOD";
		if (manager.hasGroupPermission(p, Group.ADMIN))
			message = message + " | ADMIN";
		if (manager.hasGroupPermission(p, Group.DONO))
			message = message + " | DONO";
		message = ChatColor.RED + "Use: ( " + message + " )";
		if (label.equalsIgnoreCase("tag")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("DONO")) {
					if (manager.hasGroupPermission(p, Group.DONO)) {
						tagManager.addPlayerTag(p, Tag.DONO);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag DONO");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("ADMIN")) {
					if (manager.hasGroupPermission(p, Group.ADMIN)) {
						tagManager.addPlayerTag(p, Tag.ADMIN);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag ADMIN");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("MOD")) {
					if (manager.hasGroupPermission(p, Group.MOD)) {
						tagManager.addPlayerTag(p, Tag.MOD);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag MOD");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("TRIAL")) {
					if (manager.hasGroupPermission(p, Group.TRIAL)) {
						tagManager.addPlayerTag(p, Tag.TRIAL);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag TRIAL");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("STAFF")) {
					if (manager.hasGroupPermission(p, Group.STAFF)) {
						tagManager.addPlayerTag(p, Tag.STAFF);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag STAFF");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("HELPER")) {
					if (manager.hasGroupPermission(p, Group.HELPER)) {
						tagManager.addPlayerTag(p, Tag.HELPER);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag HELPER");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("BUILDER")) {
					if (manager.hasGroupPermission(p, Group.BUILDER)) {
						tagManager.addPlayerTag(p, Tag.BUILDER);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag BUILDER");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("DEV")) {
					if (manager.hasGroupPermission(p, Group.DEV)) {
						tagManager.addPlayerTag(p, Tag.DEV);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag DEV");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("YOUTUBER")) {
					if (manager.hasGroupPermission(p, Group.YOUTUBER)) {
						tagManager.addPlayerTag(p, Tag.YOUTUBER);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag YOUTUBER");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("ULTIMATE")) {
					if (manager.hasGroupPermission(p, Group.ULTIMATE)) {
						tagManager.addPlayerTag(p, Tag.ULTIMATE);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag ULTIMATE");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("PREMIUM")) {
					if (manager.hasGroupPermission(p, Group.PREMIUM)) {
						tagManager.addPlayerTag(p, Tag.PREMIUM);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag PREMIUM");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("LIGHT")) {
					if (manager.hasGroupPermission(p, Group.LIGHT)) {
						tagManager.addPlayerTag(p, Tag.LIGHT);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag LIGHT");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("TORNEIO")) {
					if (manager.getPlugin().getTorneioManager().isParticipante(p.getUniqueId())) {
						tagManager.addPlayerTag(p, Tag.TORNEIO);
						p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag TORNEIO");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("normal")) {
					tagManager.addPlayerTag(p, Tag.NORMAL);
					p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag Normal");
					return true;
				} else if (args[0].equalsIgnoreCase("default")) {
					tagManager.addPlayerTag(p, getPlayerDefaultTag(p));
					p.sendMessage(ChatColor.GOLD + "Voce esta usando sua tag Default");
					return true;
				}
			}
			p.sendMessage(message);
			return true;
		}
		return false;
	}

	private Tag getPlayerDefaultTag(Player p) {
		PermissionManager man = manager.getPlugin().getPermissionManager();
		if (manager.getPlugin().getTorneioManager().isParticipante(p.getUniqueId()))
			return Tag.TORNEIO;
		return Tag.valueOf(man.getPlayerGroup(p).toString());
	}
}
