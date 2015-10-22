package me.flame.utils.permissions.commands;

import java.sql.SQLException;
import java.util.UUID;

import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GiveYoutuber implements CommandExecutor {
	private PermissionManager manager;

	public GiveYoutuber(PermissionManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		final Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("giveyoutuber")) {
			if (!manager.hasGroupPermission(player, Group.ADMIN)) {
				player.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
				return true;
			}
			if (args.length != 1) {
				player.sendMessage(ChatColor.RED + "Uso correto: /giveyoutuber <player>");
				return true;
			}
			final String[] argss = args;
			new BukkitRunnable() {
				@Override
				public void run() {
					@SuppressWarnings("deprecation")
					Player target = manager.getServer().getPlayer(argss[0]);
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
						player.sendMessage(ChatColor.RED + "Parece que o player nao existe!");
						return;
					}
					manager.setPlayerGroup(uuid, Group.YOUTUBER);
					try {
						manager.savePlayerGroup(uuid, Group.YOUTUBER);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					player.sendMessage(ChatColor.YELLOW + "Player " + argss[0] + "(" + uuid.toString().replace("-", "") + ") foi setado como Youtuber com sucesso!");
				}
			}.runTaskAsynchronously(manager.getPlugin());
			return true;
		}
		return false;
	}

}
