package me.flame.utils.banmanager.commands;

import me.flame.utils.banmanager.BanManagement;
import me.flame.utils.permissions.enums.Group;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kick implements CommandExecutor {

	private BanManagement manager;

	public Kick(BanManagement manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("kick")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.TRIAL)) {
					sender.sendMessage(ChatColor.RED + "Voce nao possui permissao para usar este comando!");
					return true;
				}
			}
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /kick <player> [motivo]");
				return true;
			}
			@SuppressWarnings("deprecation")
			Player target = manager.getServer().getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "O player nao esta online");
				return true;
			}
			String kickMessage = ChatColor.YELLOW + "Voce foi kickado do servidor por " + sender.getName() + "!";
			StringBuilder builder = new StringBuilder();
			boolean temmotivo = false;
			if (args.length > 1) {
				temmotivo = true;
				for (int i = 1; i < args.length; i++) {
					String espaco = " ";
					if (i >= args.length - 1)
						espaco = "";
					builder.append(args[i] + espaco);
				}
				kickMessage = kickMessage + " Motivo:" + ChatColor.AQUA + builder.toString();
			}
			if (temmotivo)
				sender.sendMessage(ChatColor.YELLOW + "O player " + target.getName() + " foi kickado. Motivo: " + ChatColor.AQUA + builder.toString());
			else
				sender.sendMessage(ChatColor.YELLOW + "O player " + target.getName() + " foi kickado. Sem motivo definido");

			for (Player player : manager.getServer().getOnlinePlayers()) {
				if (player == sender)
					continue;
				if (!manager.getPlugin().getPermissionManager().hasGroupPermission(player, Group.HELPER))
					continue;
				if (temmotivo)
					player.sendMessage(ChatColor.YELLOW + target.getName() + " foi kickado do servidor por " + sender.getName() + "! Motivo: " + ChatColor.AQUA + builder.toString());
				else
					player.sendMessage(ChatColor.YELLOW + target.getName() + " foi kickado do servidor por " + sender.getName() + "! Sem motivo definido");
			}
			target.kickPlayer(kickMessage);
		}
		return false;
	}
}
