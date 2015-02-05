package me.flame.utils.banmanager.commands;

import java.util.UUID;

import me.flame.utils.banmanager.BanManagement;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Unban implements CommandExecutor {

	private BanManagement manager;

	public Unban(BanManagement manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("unban")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.ADMIN)) {
					sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
					return true;
				}
			}
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /unban <player>");
				return true;
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					UUID uuid = null;
					try {
						uuid = UUIDFetcher.getUUIDOf(args[0]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "O player nao existe");
						return;
					}
					if (!manager.isBanned(uuid)) {
						sender.sendMessage(ChatColor.RED + "O player nao esta banido");
						return;
					}
					if (manager.getBan(uuid).isUnbanned()) {
						sender.sendMessage(ChatColor.RED + "O player nao esta banido");
						return;
					}
					sender.sendMessage(ChatColor.YELLOW + "O player " + args[0] + "(" + uuid.toString().replace("-", "") + ") foi desbanido");
					for (Player player : manager.getServer().getOnlinePlayers()) {
						if (player == sender)
							continue;
						if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.HELPER))
							continue;
						player.sendMessage(ChatColor.YELLOW + args[0] + "(" + uuid.toString().replace("-", "") + ") foi desbanido do servidor por " + sender.getName() + "!");
					}
					manager.unban(uuid);
				}
			}).start();
		}
		return false;
	}
}
