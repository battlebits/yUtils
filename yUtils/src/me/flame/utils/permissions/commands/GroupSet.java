package me.flame.utils.permissions.commands;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.utils.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GroupSet implements CommandExecutor, TabCompleter {
	private PermissionManager manager;

	public GroupSet(PermissionManager manager) {
		this.manager = manager;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return null;
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("groupset")) {
			if (!manager.hasGroupPermission(player, Group.DONO)) {
				return null;
			}
			if (args.length == 2) {
				return (List<String>) Arrays.asList(Group.values().toString());
			}
		}
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("groupset")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!manager.hasGroupPermission(player, Group.ADMIN)) {
					player.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
					return true;
				}
			}
			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /groupset <player> <grupo>");
				return true;
			}
			if (args[0].length() > 16) {
				sender.sendMessage(ChatColor.RED + "Nome muito longo, maximo de caracteres e 16");
				return true;
			}
			@SuppressWarnings("deprecation")
			Player target = manager.getServer().getPlayer(args[0]);
			Group group = Group.valueOf(args[1].toUpperCase());
			if (group == null) {
				sender.sendMessage(ChatColor.RED + "Este grupo nao existe!");
				return true;
			}
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (manager.getPlayerGroup(player) == Group.ADMIN) {
					if((group.ordinal() < 7 && group.ordinal() > 0) || group == Group.ADMIN || group == Group.DONO) {
						player.sendMessage(ChatColor.RED + "Desculpe, mas voce nao pode manipular estes grupos!");
						return true;
					}
				}
			}
			if (group == Group.NORMAL) {
				new BukkitRunnable() {
					@Override
					public void run() {
						UUID uuid = null;
						if (target != null) {
							uuid = target.getUniqueId();
						} else {
							try {
								uuid = UUIDFetcher.getUUIDOf(args[0]);
								manager.loadPlayerGroup(uuid);
							} catch (Exception e) {
							}
						}
						if (uuid == null) {
							sender.sendMessage(ChatColor.RED + "Parece que o player nao existe!");
							return;
						}
						if (group == manager.getPlayerGroup(uuid)) {
							sender.sendMessage(ChatColor.RED + "O grupo do player ja e " + group.toString());
							return;
						}
						manager.setPlayerGroup(uuid, group);
						manager.savePlayerGroup(uuid, group);
						sender.sendMessage(ChatColor.YELLOW + "Player " + args[0] + "(" + uuid.toString().replace("-", "") + ") foi setado como " + group.toString() + " com sucesso!");
						if (target == null)
							manager.removePlayer(uuid);
					}
				}.runTaskAsynchronously(manager.getPlugin());
				return true;
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					UUID uuid = null;
					if (target != null) {
						uuid = target.getUniqueId();
					} else {
						try {
							uuid = UUIDFetcher.getUUIDOf(args[0]);
							manager.loadPlayerGroup(uuid);
						} catch (Exception e) {
						}
					}
					if (uuid == null) {
						sender.sendMessage(ChatColor.RED + "Parece que o player nao existe!");
						return;
					}
					if (group == manager.getPlayerGroup(uuid)) {
						sender.sendMessage(ChatColor.RED + "O grupo do player ja e " + group.toString());
						return;
					}
					manager.setPlayerGroup(uuid, group);
					manager.savePlayerGroup(uuid, group);
					sender.sendMessage(ChatColor.YELLOW + "Player " + args[0] + "(" + uuid.toString().replace("-", "") + ") foi setado como " + group.toString() + " com sucesso!");
					if (target == null)
						manager.removePlayer(uuid);
				}
			}.runTaskAsynchronously(manager.getPlugin());
			return true;
		}
		return false;
	}

}
