package me.flame.utils.banmanager.commands;

import java.sql.SQLException;
import java.util.UUID;

import me.flame.utils.banmanager.BanManagement;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.utils.DateUtils;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TempBan implements CommandExecutor {

	private BanManagement manager;

	public TempBan(BanManagement manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("tempban")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.TRIAL)) {
					sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
					return true;
				}
			}
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /tempban <player> <tempo> <motivo>");
				return true;
			}
			@SuppressWarnings("deprecation")
			final Player target = manager.getServer().getPlayer(args[0]);
			if (target == null) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (manager.getPlugin().getPermissionManager().isGroup(player, Group.TRIAL)) {
						sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para banir players offline!");
						return true;
					}
				}
			}
			final String[] argss = args;
			final CommandSender senderr = sender;
			new BukkitRunnable() {
				@Override
				public void run() {
					PermissionManager permManager = manager.getPlugin().getPermissionManager();
					UUID uuid = null;
					if (target != null) {
						uuid = target.getUniqueId();
					} else {
						try {
							uuid = UUIDFetcher.getUUIDOf(argss[0]);
						} catch (Exception e) {
							senderr.sendMessage(ChatColor.RED + "O player nao existe");
							return;
						}
					}
					if (manager.isBanned(uuid)) {
						if (!manager.getBan(uuid).isUnbanned()) {
							senderr.sendMessage(ChatColor.RED + "O player ja esta banido");
							return;
						}
					}
					if (permManager.getPlayerGroup(uuid).ordinal() >= 5 && senderr instanceof Player && permManager.getPlayerGroup((Player) senderr) != Group.DONO && permManager.getPlayerGroup((Player) senderr) != Group.ADMIN) {
						senderr.sendMessage(ChatColor.RED + "Voce nao pode banir uma staff");
						return;
					}
					long expiresCheck;
					try {
						expiresCheck = DateUtils.parseDateDiff(argss[1], true);
					} catch (Exception e1) {
						senderr.sendMessage("Formato invalido");
						return;
					}
					String tempo = DateUtils.formatDifference((expiresCheck - System.currentTimeMillis()) / 1000);
					StringBuilder builder = new StringBuilder();
					for (int i = 2; i < argss.length; i++) {
						String espaco = " ";
						if (i >= argss.length - 1)
							espaco = "";
						builder.append(argss[i] + espaco);
					}
					senderr.sendMessage(ChatColor.YELLOW + "O player " + argss[0] + "(" + uuid.toString().replace("-", "") + ") foi temporariamente banido por " + tempo + ". Motivo: " + ChatColor.AQUA + builder.toString());
					for (Player player : manager.getServer().getOnlinePlayers()) {
						if (player == senderr)
							continue;
						if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.HELPER))
							continue;
						player.sendMessage(ChatColor.YELLOW + argss[0] + "(" + uuid.toString().replace("-", "") + ") foi temporariamente banido do servidor por " + senderr.getName() + ". Banimento durara " + tempo + "! Motivo: " + ChatColor.AQUA + builder.toString());
					}
					if (target != null) {
						String kickMessage = ChatColor.YELLOW + "Voce foi temporariamente banido do servidor por " + senderr.getName() + ".\nBanimendo durara " + tempo + "!\nMotivo: " + ChatColor.AQUA + builder.toString();
						kickPlayer(target, kickMessage);
					}
					try {
						manager.ban(new me.flame.utils.banmanager.constructors.Ban(uuid, senderr.getName(), builder.toString(), System.currentTimeMillis(), expiresCheck, false));
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(manager.getPlugin());
		}
		return false;
	}

	public void kickPlayer(final Player player, final String message) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.kickPlayer(message);
			}
		}.runTask(manager.getPlugin());
	}
}
