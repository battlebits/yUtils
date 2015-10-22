package me.flame.utils.banmanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.banmanager.commands.Kick;
import me.flame.utils.banmanager.commands.TempBan;
import me.flame.utils.banmanager.commands.TempMute;
import me.flame.utils.banmanager.commands.Unban;
import me.flame.utils.banmanager.commands.Unmute;
import me.flame.utils.banmanager.constructors.Ban;
import me.flame.utils.banmanager.constructors.Mute;
import me.flame.utils.banmanager.listeners.LoginListener;
import me.flame.utils.utils.DateUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.MapMaker;

public class BanManagement extends Management {
	private Map<UUID, Ban> banimentos = new MapMaker().softValues().makeMap();
	private Map<UUID, Mute> mutados = new MapMaker().softValues().makeMap();

	public BanManagement(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new LoginListener(this), getPlugin());
		getPlugin().getCommand("kick").setExecutor(new Kick(this));
		getPlugin().getCommand("ban").setExecutor(new me.flame.utils.banmanager.commands.Ban(this));
		getPlugin().getCommand("unban").setExecutor(new Unban(this));
		getPlugin().getCommand("mute").setExecutor(new me.flame.utils.banmanager.commands.Mute(this));
		getPlugin().getCommand("unmute").setExecutor(new Unmute(this));
		getPlugin().getCommand("tempban").setExecutor(new TempBan(this));
		getPlugin().getCommand("tempmute").setExecutor(new TempMute(this));
	}

	public void loadBanAndMute(UUID uuid) throws SQLException {
		PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Banimentos` WHERE uuid='" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		while (result.next()) {
			String bannedBy = result.getString("banned_By");
			String reason = result.getString("reason");
			boolean unbanned = result.getInt("unbanned") == 1;
			long expire = result.getLong("expire");
			long ban_time = result.getLong("ban_time");
			Ban ban = banimentos.get(uuid);
			if (ban == null)
				ban = new Ban(uuid, bannedBy, reason, ban_time, expire, unbanned);
			else
				ban.setNewBan(bannedBy, reason, ban_time, expire, unbanned);
			banimentos.put(ban.getBannedUuid(), ban);
		}
		stmt = getMySQL().prepareStatement("SELECT * FROM `Mutes` WHERE uuid='" + uuid.toString().replace("-", "") + "';");
		result = stmt.executeQuery();
		while (result.next()) {
			String mutedBy = result.getString("muted_By");
			String reason = result.getString("reason");
			long expire = result.getLong("expire");
			long mute_time = result.getLong("mute_time");
			Mute mute = new Mute(uuid, mutedBy, reason, mute_time, expire);
			mutados.put(mute.getMutedUuid(), mute);
		}
		result.close();
		stmt.close();
		if (!isBanned(uuid))
			return;
		final Ban ban = getBan(uuid);
		if (ban.isUnbanned())
			return;
		if (ban.hasExpired()) {
			removeTempban(ban.getBannedUuid());
			return;
		}
	}

	public boolean isBanned(Player player) {
		UUID uuid = player.getUniqueId();
		return banimentos.containsKey(uuid);
	}

	public boolean isBanned(UUID uuid) {
		return banimentos.containsKey(uuid);
	}

	public Ban getBan(Player player) {
		if (!isBanned(player))
			return null;
		UUID uuid = player.getUniqueId();
		return banimentos.get(uuid);
	}

	public Ban getBan(UUID uuid) {
		if (!isBanned(uuid))
			return null;
		return banimentos.get(uuid);
	}

	public boolean isMuted(Player player) {
		UUID uuid = player.getUniqueId();
		return mutados.containsKey(uuid);
	}

	public Mute getMute(Player player) {
		if (!isMuted(player))
			return null;
		UUID uuid = player.getUniqueId();
		return mutados.get(uuid);
	}

	public boolean isMuted(UUID uuid) {
		return mutados.containsKey(uuid);
	}

	public Mute getMute(UUID uuid) {
		if (!isMuted(uuid))
			return null;
		return mutados.get(uuid);
	}

	public void ban(final Ban ban) throws SQLException {
		banimentos.put(ban.getBannedUuid(), ban);
		Statement stmt = getMySQL().createStatement();
		stmt.executeUpdate("INSERT INTO `Banimentos`(`uuid`, `banned_By`, `reason`, `expire`, `ban_time`, `unbanned`) " + "VALUES ('" + ban.getBannedUuid().toString().replace("-", "") + "' , '" + ban.getBannedBy() + "' , '" + ban.getReason() + "' , '" + ban.getDuration() + "', '" + ban.getBanTime() + "', 0);");
		stmt.close();
	}

	public void mute(final Mute mute) throws SQLException {
		mutados.put(mute.getMutedUuid(), mute);
		Statement stmt = getMySQL().createStatement();
		stmt.executeUpdate("INSERT INTO `Mutes`(`uuid`, `muted_By`, `reason`, `expire`, `mute_time`) VALUES ('" + mute.getMutedUuid().toString().replace("-", "") + "', '" + mute.getMutedBy() + "', '" + mute.getReason() + "' , '" + mute.getDuration() + "', '" + mute.getMuteTime() + "');");
		stmt.close();
	}

	public boolean unmute(final UUID uuid) throws SQLException {
		if (!isMuted(uuid))
			return false;
		mutados.remove(uuid);
		Statement stmt = getMySQL().createStatement();
		stmt.executeUpdate("DELETE FROM `Mutes` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
		stmt.close();
		return true;
	}

	public boolean unban(final UUID uuid) throws SQLException {
		if (!isBanned(uuid))
			return false;
		Ban ban = getBan(uuid);
		ban.unban();
		Statement stmt = getMySQL().createStatement();
		stmt.executeUpdate("UPDATE `Banimentos` SET `unbanned`=1 WHERE `uuid`='" + uuid.toString().replace("-", "") + "' and `unbanned`=0;");
		stmt.close();
		return true;
	}

	public boolean removeTempban(final UUID uuid) throws SQLException {
		if (!isBanned(uuid))
			return false;
		Ban ban = getBan(uuid);
		ban.unban();
		Statement stmt = getMySQL().createStatement();
		stmt.executeUpdate("DELETE FROM `Banimentos` WHERE `uuid`='" + uuid.toString().replace("-", "") + "' and `expire`!=0;");
		stmt.close();
		return true;
	}

	public String getBanMessage(Ban ban) {
		StringBuilder builder = new StringBuilder();
		if (ban.isPermanent()) {
			builder.append(ChatColor.YELLOW + "Voce foi banido do servidor!");
			builder.append("\n" + ban.getBannedBy() + " baniu voce! Motivo: " + ChatColor.AQUA + ban.getReason());
		} else {
			String tempo = DateUtils.formatDifference((ban.getDuration() - System.currentTimeMillis()) / 1000);
			builder.append(ChatColor.YELLOW + "Voce foi temporariamente banido do servidor!");
			builder.append("\nBanimento durara " + tempo);
			builder.append("\n" + ban.getBannedBy() + " baniu voce! Motivo: " + ChatColor.AQUA + ban.getReason());
		}
		return builder.toString();
	}

	@Override
	public void onDisable() {
		banimentos.clear();
		mutados.clear();
	}
}
