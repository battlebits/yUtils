package me.flame.utils.permissions.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.ChatColor;
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
			if (manager.hasGroupPermission(player, Group.DONO)) {
				if (args.length == 2) {
					List<String> str = new ArrayList<String>();
					for (Group group : Group.values()) {
						str.add(group.toString().toLowerCase());
					}
					return str;
				}
			} else if (manager.hasGroupPermission(player, Group.ADMIN)) {
				if (args.length == 2) {
					List<String> str = new ArrayList<String>();
					str.add(Group.MOD.toString().toLowerCase());
					str.add(Group.TRIAL.toString().toLowerCase());
					str.add(Group.HELPER.toString().toLowerCase());
					str.add(Group.NORMAL.toString().toLowerCase());
					str.add(Group.HELPER.toString().toLowerCase());
					return str;
				}
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
			@SuppressWarnings({ "deprecation" })
			final Player target = manager.getServer().getPlayer(args[0]);
			Group groupo = null;
			try {
				groupo = Group.valueOf(args[1].toUpperCase());
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Este grupo nao existe!");
				return true;
			}
			final Group group = groupo;
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (manager.getPlayerGroup(player) == Group.ADMIN) {
					if ((group.ordinal() < 5 && group.ordinal() > 0) || group == Group.STAFF || group == Group.ADMIN || group == Group.DONO) {
						player.sendMessage(ChatColor.RED + "Desculpe, mas voce nao pode manipular estes grupos!");
						return true;
					}
				}
			}
			final String[] argss = args;
			final CommandSender senderr = sender;
			if (group == Group.NORMAL) {
				new BukkitRunnable() {
					@Override
					public void run() {
						UUID uuid = null;
						if (target != null) {
							uuid = target.getUniqueId();
						} else {
							try {
								uuid = UUIDFetcher.getUUIDOf(argss[0]);
							} catch (Exception e) {
							}
						}
						if (uuid == null) {
							senderr.sendMessage(ChatColor.RED + "Parece que o player nao existe!");
							return;
						}
						if (senderr instanceof Player) {
							Player player = (Player) senderr;
							if (manager.getPlayerGroup(uuid) == Group.DONO && manager.getPlayerGroup(player) == Group.ADMIN) {
								senderr.sendMessage(ChatColor.RED + "Voce nao pode mudar o grupo de um dono");
								return;
							}
						}
						if (group == manager.getPlayerGroup(uuid)) {
							senderr.sendMessage(ChatColor.RED + "O grupo do player ja e " + group.toString());
							return;
						}
						manager.removePlayerGroup(uuid);
						manager.removePlayer(uuid);
						senderr.sendMessage(ChatColor.YELLOW + "Player " + argss[0] + "(" + uuid.toString().replace("-", "") + ") foi setado como " + group.toString() + " com sucesso!");
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
							uuid = UUIDFetcher.getUUIDOf(argss[0]);
						} catch (Exception e) {
						}
					}
					if (uuid == null) {
						senderr.sendMessage(ChatColor.RED + "Parece que o player nao existe!");
						return;
					}
					if (senderr instanceof Player) {
						Player player = (Player) senderr;
						if (manager.getPlayerGroup(uuid) == Group.DONO && manager.getPlayerGroup(player) == Group.ADMIN) {
							senderr.sendMessage(ChatColor.RED + "Voce nao pode mudar o grupo de um dono");
							return;
						}
					}
					if (group == manager.getPlayerGroup(uuid)) {
						senderr.sendMessage(ChatColor.RED + "O grupo do player ja e " + group.toString());
						return;
					}
					manager.setPlayerGroup(uuid, group);
					manager.savePlayerGroup(uuid, group);
					senderr.sendMessage(ChatColor.YELLOW + "Player " + argss[0] + "(" + uuid.toString().replace("-", "") + ") foi setado como " + group.toString() + " com sucesso!");
				}
			}.runTaskAsynchronously(manager.getPlugin());
			return true;
		}
		return false;
	}

}
