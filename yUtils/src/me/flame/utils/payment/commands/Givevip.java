package me.flame.utils.payment.commands;

import java.util.UUID;

import me.flame.utils.payment.BuyManager;
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

public class Givevip implements CommandExecutor {

	private BuyManager manager;

	public Givevip(BuyManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("givevip")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.DONO)) {
					sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
					return true;
				}
			}
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /givevip <player> <tempo> <grupo>");
				return true;
			}
			new BukkitRunnable() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					Player target = manager.getServer().getPlayer(args[0]);
					PermissionManager permManager = manager.getPlugin().getPermissionManager();
					UUID uuid = null;
					if (target != null) {
						uuid = target.getUniqueId();
					} else {
						try {
							uuid = UUIDFetcher.getUUIDOf(args[0]);
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "O player nao existe");
							return;
						}
					}
					long expiresCheck;
					try {
						expiresCheck = DateUtils.parseDateDiff(args[1], true);
					} catch (Exception e1) {
						sender.sendMessage("Formato invalido");
						return;
					}
					String tempo = DateUtils.formatDifference((expiresCheck - System.currentTimeMillis()) / 1000);
					Group grupo = null;
					try {
						grupo = Group.valueOf(args[2].toUpperCase());
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Este grupo nao existe!");
						return;
					}
					if (grupo.ordinal() > 3 || grupo == Group.NORMAL) {
						sender.sendMessage(ChatColor.RED + "Voce nao pode usar este grupo!");
						return;
					}
					permManager.setPlayerGroup(uuid, grupo);
					permManager.savePlayerGroup(uuid, grupo);
					manager.addExpire(uuid, grupo, expiresCheck);
					sender.sendMessage(ChatColor.YELLOW + "O player " + args[0] + "(" + uuid.toString() + ") teve vip setado por " + tempo);
					if (target != null) {
						target.sendMessage(ChatColor.YELLOW + "---------------------------BATTLEBITS------------------------------");
						target.sendMessage("");
						target.sendMessage(ChatColor.YELLOW + "Seu pagamento foi detectado e voce ja recebeu seu " + grupo.toString() + "!");
						target.sendMessage("");
						target.sendMessage(ChatColor.YELLOW + "-------------------------------------------------------------------");
					}
				}
			}.runTaskAsynchronously(manager.getPlugin());
		}
		return false;
	}
}
