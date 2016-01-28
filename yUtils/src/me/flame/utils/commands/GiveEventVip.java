package me.flame.utils.commands;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.flame.utils.Main;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.tagmanager.TagManager;
import me.flame.utils.utils.DateUtils;
import me.flame.utils.utils.UUIDFetcher;

public class GiveEventVip implements CommandExecutor {

	private Main main;

	public GiveEventVip(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("giveeventvip")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!main.getPermissionManager().hasGroupPermission(player, Group.STREAMER)) {
					sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
					return true;
				}
			}
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /giveeventvip <player>");
				return true;
			}
			final String[] argss = args;
			final CommandSender senderr = sender;
			new BukkitRunnable() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					final Player target = main.getServer().getPlayer(argss[0]);
					PermissionManager permManager = main.getPermissionManager();
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
					long expiresCheck;
					try {
						expiresCheck = DateUtils.parseDateDiff("7d", true);
					} catch (Exception e1) {
						senderr.sendMessage("Formato invalido");
						return;
					}
					String tempo = DateUtils.formatDifference((expiresCheck - System.currentTimeMillis()) / 1000);
					Group grupo = Group.ULTIMATE;
					permManager.setPlayerGroup(uuid, grupo);
					try {
						permManager.savePlayerGroup(uuid, grupo);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					try {
						main.getBuyManager().addExpire(uuid, grupo, expiresCheck);
					} catch (Exception e) {
						senderr.sendMessage(ChatColor.RED + "Erro ao tentar adicionar VIP : " + e.getCause().toString());
					}
					senderr.sendMessage(ChatColor.YELLOW + "O player " + argss[0] + "(" + uuid.toString() + ") teve vip setado por " + tempo);
					if (target != null) {
						new BukkitRunnable() {
							@Override
							public void run() {
								main.getTagManager().addPlayerTag(target, TagManager.getPlayerDefaultTag(target));
							}
						}.runTask(main);
					}
				}
			}.runTaskAsynchronously(main);
		}
		return false;
	}
}