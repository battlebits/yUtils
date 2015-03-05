package me.flame.utils.torneio.commands;

import java.util.UUID;

import me.flame.utils.permissions.enums.Group;
import me.flame.utils.torneio.TorneioManager;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AddTorneio implements CommandExecutor {
	private TorneioManager manager;

	public AddTorneio(TorneioManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("addtorneio")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.DONO)) {
					sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
					return true;
				}
			}
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /addtorneio <player>");
				return true;
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					@SuppressWarnings("deprecation")
					Player target = manager.getServer().getPlayer(args[0]);
					UUID uuid = null;
					if (target != null) {
						uuid = target.getUniqueId();
					} else {
						try {
							uuid = UUIDFetcher.getUUIDOf(args[0]);
						} catch (Exception e) {
						}
					}
					if (uuid == null) {
						sender.sendMessage(ChatColor.RED + "Parece que o player nao existe!");
						return;
					}
					manager.addPlayerOnTorneio(uuid);
					sender.sendMessage(ChatColor.YELLOW + "Player " + args[0] + "(" + uuid.toString().replace("-", "") + ") foi adicionado a lista de participantes do torneio");
				}
			}.runTaskAsynchronously(manager.getPlugin());
			return true;
		}
		return false;
	}

}
