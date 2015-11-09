package me.flame.utils.banmanager.commands;

import java.sql.SQLException;
import java.util.UUID;

import me.flame.utils.banmanager.BanManagement;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Ban implements CommandExecutor {

	private BanManagement manager;

	public Ban(BanManagement manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ban")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.TRIAL)) {
					sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
					return true;
				}
			}
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /ban <player> <motivo>");
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
					try {
						manager.loadBanAndMute(uuid);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					try {
						if (manager.isBanned(uuid)) {
							if (!manager.getBan(uuid).isUnbanned()) {
								senderr.sendMessage(ChatColor.RED + "O player ja esta banido");
								return;
							}
						}
					} catch (SQLException e1) {
						senderr.sendMessage(ChatColor.RED + "Erro ao conectar ao banco de dados");
						return;
					}
					if (permManager.getPlayerGroup(uuid).ordinal() >= 5 && senderr instanceof Player && permManager.getPlayerGroup((Player) senderr) != Group.DONO && permManager.getPlayerGroup((Player) senderr) != Group.ADMIN) {
						senderr.sendMessage(ChatColor.RED + "Voce nao pode banir uma staff");
						return;
					}
					StringBuilder builder = new StringBuilder();
					for (int i = 1; i < argss.length; i++) {
						String espaco = " ";
						if (i >= argss.length - 1)
							espaco = "";
						builder.append(argss[i] + espaco);
					}
					senderr.sendMessage(ChatColor.YELLOW + "O player " + argss[0] + "(" + uuid.toString().replace("-", "") + ") foi banido. Motivo: " + ChatColor.AQUA + builder.toString());
					for (Player player : manager.getServer().getOnlinePlayers()) {
						if (player == senderr)
							continue;
						if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.HELPER))
							continue;
						player.sendMessage(ChatColor.YELLOW + argss[0] + "(" + uuid.toString().replace("-", "") + ") foi banido do servidor por " + senderr.getName() + "! Motivo: " + ChatColor.AQUA + builder.toString());
					}
					if (target != null) {
						String kickMessage = ChatColor.YELLOW + "Voce foi banido do servidor por " + senderr.getName() + "! Motivo: " + ChatColor.AQUA + builder.toString();
						kickPlayer(target, kickMessage);
					}
					try {
						manager.ban(new me.flame.utils.banmanager.constructors.Ban(uuid, senderr.getName(), builder.toString(), System.currentTimeMillis(), 0, false));
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
