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

public class Unmute implements CommandExecutor {

	private BanManagement manager;

	public Unmute(BanManagement manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("unmute")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.ADMIN)) {
					sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
					return true;
				}
			}
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /unmute <player>");
				return true;
			}
			@SuppressWarnings("deprecation")
			final Player target = manager.getServer().getPlayer(args[0]);
			final String[] argss = args;
			final CommandSender senderr = sender;
			new Thread(new Runnable() {
				@Override
				public void run() {
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
					if (!manager.isMuted(uuid)) {
						senderr.sendMessage(ChatColor.RED + "O player nao esta mutado");
						return;
					}
					senderr.sendMessage(ChatColor.YELLOW + "O player " + argss[0] + "(" + uuid.toString().replace("-", "") + ") foi desmutado!");
					for (Player player : manager.getServer().getOnlinePlayers()) {
						if (player == senderr)
							continue;
						if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.HELPER))
							continue;
						player.sendMessage(ChatColor.YELLOW + argss[0] + "(" + uuid.toString().replace("-", "") + ") foi desmutado do servidor por " + senderr.getName() + "!");
					}
					if (target != null) {
						target.sendMessage(ChatColor.YELLOW + "Voce foi desmutado do servidor por " + senderr.getName() + "! Agora voce ja pode falar");
					}
					try {
						manager.unmute(uuid);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		return false;
	}
}
