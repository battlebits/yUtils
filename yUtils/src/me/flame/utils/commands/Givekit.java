package me.flame.utils.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Givekit implements CommandExecutor {
	// givekit {name} {kit}

	private Main main;

	public Givekit(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("givekit")) {
			if (sender instanceof Player && !main.getPermissionManager().hasGroupPermission((Player) sender, Group.STREAMER))
				return true;
			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Uso correto: /givekit <player> <kit>");
				return true;
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					@SuppressWarnings("deprecation")
					Player target = main.getServer().getPlayer(args[0]);
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
					String kit = args[1].toLowerCase();
					try {
						Connection con = trySQLConnection();
						Statement stmt = con.createStatement();
						stmt.executeUpdate("INSERT INTO `Kits`(`Player`, `Kits`) VALUES ('" + uuid.toString().replace("-", "") + "','" + kit + "');");
						stmt.close();
						con.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					sender.sendMessage(ChatColor.YELLOW + "Player " + args[0] + "(" + uuid.toString().replace("-", "") + ") teve o kit do HG '" + kit + "' adicionado!");
				}
			}.runTaskAsynchronously(main);
			return true;
		}
		return false;
	}

	@SuppressWarnings("static-method")
	private synchronized Connection trySQLConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String conn = "jdbc:mysql://localhost:3306/hungergames";
			return DriverManager.getConnection(conn, "root", "saobestanime");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
