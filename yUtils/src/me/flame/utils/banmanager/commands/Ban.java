package me.flame.utils.banmanager.commands;

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
			Player target = manager.getServer().getPlayer(args[0]);
			if (target == null) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (manager.getPlugin().getPermissionManager().isGroup(player, Group.TRIAL)) {
						sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para banir players offline!");
						return true;
					}
				}
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					PermissionManager permManager = manager.getPlugin().getPermissionManager();
					UUID uuid = null;
					if (target != null) {
						uuid = target.getUniqueId();
					} else {
						try {
							uuid = UUIDFetcher.getUUIDOf(args[0]);
							permManager.loadPlayerGroup(uuid);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "O player nao existe");
							return;
						}
					}
					if (manager.isBanned(uuid)) {
						if (!manager.getBan(uuid).isUnbanned()) {
							sender.sendMessage(ChatColor.RED + "O player ja esta banido");
							return;
						}
					}
					if (permManager.getPlayerGroup(uuid).ordinal() >= 5 && sender instanceof Player && permManager.getPlayerGroup((Player) sender) != Group.DONO && permManager.getPlayerGroup((Player) sender) != Group.ADMIN) {
						sender.sendMessage(ChatColor.RED + "Voce nao pode banir uma staff");
						return;
					}
					StringBuilder builder = new StringBuilder();
					for (int i = 1; i < args.length; i++) {
						String espaco = " ";
						if (i >= args.length - 1)
							espaco = "";
						builder.append(args[i] + espaco);
					}
					sender.sendMessage(ChatColor.YELLOW + "O player " + args[0] + "(" + uuid.toString().replace("-", "") + ") foi banido. Motivo: " + ChatColor.AQUA + builder.toString());
					for (Player player : manager.getServer().getOnlinePlayers()) {
						if (player == sender)
							continue;
						if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.HELPER))
							continue;
						player.sendMessage(ChatColor.YELLOW + args[0] + "(" + uuid.toString().replace("-", "") + ") foi banido do servidor por " + sender.getName() + "! Motivo: " + ChatColor.AQUA + builder.toString());
					}
					if (target != null) {
						String kickMessage = ChatColor.YELLOW + "Voce foi banido do servidor por " + sender.getName() + "! Motivo: " + ChatColor.AQUA + builder.toString();
						target.kickPlayer(kickMessage);
					} else {
						permManager.removePlayerGroup(uuid);
					}
					manager.ban(new me.flame.utils.banmanager.constructors.Ban(uuid, sender.getName(), builder.toString(), System.currentTimeMillis(), 0, false));
				}
			}).start();
		}
		return false;
	}
}
