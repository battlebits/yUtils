package me.flame.utils.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.banmanager.constructors.Ban;
import me.flame.utils.banmanager.constructors.Mute;
import me.flame.utils.payment.constructors.Expire;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.permissions.enums.ServerType;
import me.flame.utils.tagmanager.enums.Tag;
import me.flame.utils.utils.DateUtils;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Account implements CommandExecutor {

	private Main main;

	public Account(Main main) {
		this.main = main;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("account")) {
			Player targett = null;
			String name = "";
			if (args.length == 0 && sender instanceof Player) {
				targett = (Player) sender;
				name = targett.getName();
			} else if (args.length == 1) {
				if (args[0].length() > 16) {
					sender.sendMessage(ChatColor.RED + "Nome muito longo, maximo de caracteres e 16");
					return true;
				}
				targett = main.getServer().getPlayer(args[0]);
				name = args[0];
			} else {
				sender.sendMessage(ChatColor.RED + "Uso correto: /account <player>");
				return true;
			}
			final Player target = targett;
			final String[] argss = args;
			final CommandSender senderr = sender;
			final String namee = name;
			new BukkitRunnable() {
				@Override
				public void run() {
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
						senderr.sendMessage(ChatColor.RED + "Parece que o player nao existe!");
						return;
					}
					Group group = Group.NORMAL;
					HashMap<ServerType, Group> serverStaff = new HashMap<>();
					try {
						PreparedStatement stmt = null;
						ResultSet result = null;
						for (ServerType type : ServerType.values()) {
							if (type == ServerType.NONE)
								continue;
							stmt = main.mainConnection.prepareStatement("SELECT * FROM `Staff-" + type.toString() + "` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
							result = stmt.executeQuery();
							if (result.next()) {
								try {
									Group grupo = Group.valueOf(result.getString("rank").toUpperCase());
									serverStaff.put(type, grupo);
								} catch (Exception e) {
									System.out.println("Staff-" + type.toString() + " " + result.getString("id") + " esta bugado");
									e.printStackTrace();
								}
							}
						}
						if (result != null)
							result.close();
						if (stmt != null)
							stmt.close();
						stmt = main.mainConnection.prepareStatement("SELECT * FROM `Ranks` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
						result = stmt.executeQuery();
						if (result.next()) {
							try {
								group = Group.valueOf(result.getString("rank").toUpperCase());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (result != null)
							result.close();
						if (stmt != null)
							stmt.close();
					} catch (Exception e) {
						main.getLogger().info("Nao foi possivel carregar grupos");
						e.printStackTrace();
					}
					Expire vipExpire = main.getBuyManager().getExpire(uuid);
					Ban ban = null;
					try {
						ban = main.getBanManager().getBan(uuid);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					Mute mute = main.getBanManager().getMute(uuid);
					senderr.sendMessage(ChatColor.YELLOW + "Informacoes sobre o Jogador " + namee + "(" + uuid.toString().replace("-", "") + ")");
					senderr.sendMessage(ChatColor.YELLOW + "---------------------------------------------");
					if (!main.getRankingManager().containsAccount(uuid))
						try {
							main.getRankingManager().loadAccount(uuid);
						} catch (Exception e) {
							senderr.sendMessage(ChatColor.RED + "Nao foi possivel carregar algumas informacoes");
						}
					me.flame.utils.ranking.constructors.Account account = main.getRankingManager().getAccount(uuid);
					senderr.sendMessage(ChatColor.YELLOW + "Liga atual: " + account.getLiga().getSymbol() + " " + account.getLiga().toString());
					senderr.sendMessage(ChatColor.YELLOW + "XP: " + account.getXp());
					if (senderr == target) {
						senderr.sendMessage(ChatColor.YELLOW + "Money: " + account.getMoney());
						senderr.sendMessage(ChatColor.YELLOW + "Fichas: " + account.getFichas());
					}
					if (vipExpire != null) {
						String tempo = DateUtils.formatDifference((vipExpire.getExpire() - System.currentTimeMillis()) / 1000);
						senderr.sendMessage(ChatColor.YELLOW + "VIP " + Tag.valueOf(vipExpire.getGroup().toString()).getPrefix() + ChatColor.YELLOW + "expira em " + tempo);
					} else {
						if (group != Group.NORMAL)
							senderr.sendMessage(ChatColor.YELLOW + "Grupo do servidor atual: " + Tag.valueOf(group.toString()).getPrefix());
					}
					for (Entry<ServerType, Group> staff : serverStaff.entrySet()) {
						senderr.sendMessage(Tag.valueOf(staff.getValue().toString()).getPrefix() + ChatColor.YELLOW + "do server " + staff.getKey().toString());
					}
					if (ban != null) {
						if (!ban.isUnbanned()) {
							if (!ban.isPermanent()) {
								String tempo = DateUtils.formatDifference((ban.getDuration() - System.currentTimeMillis()) / 1000);
								senderr.sendMessage(ChatColor.YELLOW + "Foi banido por " + ban.getBannedBy() + " e durara " + tempo);
							} else {
								senderr.sendMessage(ChatColor.YELLOW + "Foi banido permanentemente por " + ban.getBannedBy());
							}
						}
					}
					if (mute != null) {
						if (!mute.isPermanent()) {
							String tempo = DateUtils.formatDifference((mute.getDuration() - System.currentTimeMillis()) / 1000);
							senderr.sendMessage(ChatColor.YELLOW + "Foi mutado por " + mute.getMutedBy() + " e durara " + tempo);
						} else {
							senderr.sendMessage(ChatColor.YELLOW + "Foi mutado permanentemente por " + mute.getMutedBy());
						}
					}
					senderr.sendMessage(ChatColor.YELLOW + "---------------------------------------------");
				}
			}.runTaskAsynchronously(main);
			return true;
		}
		return false;
	}

}
