package me.flame.utils.banmanager.commands;

import java.util.UUID;

import me.flame.utils.banmanager.BanManagement;
import me.flame.utils.banmanager.utils.DateUtils;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempMute implements CommandExecutor {

	private BanManagement manager;

	public TempMute(BanManagement manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("tempmute")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.TRIAL)) {
					sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
					return true;
				}
			}
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /tempmute <player> <tempo> <motivo>");
				return true;
			}
			@SuppressWarnings("deprecation")
			Player target = manager.getServer().getPlayer(args[0]);
			if (target == null) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (manager.getPlugin().getPermissionManager().isGroup(player, Group.TRIAL)) {
						sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para mutar players offline!");
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
					if (manager.isMuted(uuid)) {
						sender.sendMessage(ChatColor.RED + "O player ja esta mutado");
						return;
					}
					if (permManager.getPlayerGroup(uuid).ordinal() >= 5 && sender instanceof Player && permManager.getPlayerGroup((Player) sender) != Group.DONO && permManager.getPlayerGroup((Player) sender) != Group.ADMIN) {
						sender.sendMessage(ChatColor.RED + "Voce nao pode mutar uma staff");
						return;
					}
					long expiresCheck;
					try {
						expiresCheck = DateUtils.parseDateDiff(args[1], true);
					} catch (Exception e1) {
						sender.sendMessage("Formato invalido");
						return;
					}
					String tempo = DateUtils.formatDifference((expiresCheck - System.currentTimeMillis()) / 1000);
					StringBuilder builder = new StringBuilder();
					for (int i = 2; i < args.length; i++) {
						String espaco = " ";
						if (i >= args.length - 1)
							espaco = "";
						builder.append(args[i] + espaco);
					}
					sender.sendMessage(ChatColor.YELLOW + "O player " + args[0] + "(" + uuid.toString().replace("-", "") + ") foi temporariamente mutado por " + tempo + ". Motivo: " + ChatColor.AQUA + builder.toString());
					for (Player player : manager.getServer().getOnlinePlayers()) {
						if (player == sender)
							continue;
						if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.HELPER))
							continue;
						player.sendMessage(ChatColor.YELLOW + args[0] + "(" + uuid.toString().replace("-", "") + ") foi temporariamente mutado por " + sender.getName() + ". Mute durara " + tempo + "! Motivo: " + ChatColor.AQUA + builder.toString());
					}
					if (target != null) {
						target.sendMessage(ChatColor.YELLOW + "Voce foi temporariamente mutado por " + tempo + " pelo player " + sender.getName() + "! Motivo: " + ChatColor.AQUA + builder.toString());
					}
					manager.mute(new me.flame.utils.banmanager.constructors.Mute(uuid, sender.getName(), builder.toString(), System.currentTimeMillis(), expiresCheck));
				}
			}).start();
		}
		return false;
	}
}
