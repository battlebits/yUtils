package me.flame.utils.banmanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
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
import me.flame.utils.mysql.Connect;
import me.flame.utils.permissions.enums.ServerType;
import me.flame.utils.utils.DateUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BanManagement extends Management {
	private Map<UUID, Ban> banimentos;
	private Map<UUID, Mute> mutados;

	public BanManagement(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		banimentos = new HashMap<>();
		mutados = new HashMap<>();
		getServer().getPluginManager().registerEvents(new LoginListener(this), getPlugin());
		getPlugin().getCommand("kick").setExecutor(new Kick(this));
		getPlugin().getCommand("ban").setExecutor(new me.flame.utils.banmanager.commands.Ban(this));
		getPlugin().getCommand("unban").setExecutor(new Unban(this));
		getPlugin().getCommand("mute").setExecutor(new me.flame.utils.banmanager.commands.Mute(this));
		getPlugin().getCommand("unmute").setExecutor(new Unmute(this));
		getPlugin().getCommand("tempban").setExecutor(new TempBan(this));
		getPlugin().getCommand("tempmute").setExecutor(new TempMute(this));
	}

	public void loadBanAndMute(UUID uuid) throws Exception {
		Connect.lock.lock();
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
		stmt.close();
		result.close();
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
		Connect.lock.unlock();
		if (!banimentos.containsKey(uuid))
			return;
		final Ban ban = banimentos.get(uuid);
		if (ban.isUnbanned())
			return;
		if (ban.hasExpired()) {
			removeTempban(ban.getBannedUuid());
			return;
		}
	}

	public boolean isBanned(Player player) throws Exception {
		return isBanned(player.getUniqueId());
	}

	public boolean isBanned(UUID uuid) throws Exception {
		if(banimentos.containsKey(uuid))
			return true;
		loadBanAndMute(uuid);
		return banimentos.containsKey(uuid);
	}

	public Ban getBan(Player player) throws Exception {
		if (!isBanned(player))
			return null;
		return getBan(player.getUniqueId());
	}

	public Ban getBan(UUID uuid) throws Exception {
		if(banimentos.containsKey(uuid))
			return banimentos.get(uuid);
		loadBanAndMute(uuid);
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

	public void ban(final Ban ban) throws Exception {
		if(getPlugin().getServerType() == ServerType.TESTSERVER) {
			throw new Exception("Servidor de Teste");
		}
		banimentos.put(ban.getBannedUuid(), ban);
		Connect.lock.lock();
		Statement stmt = getMySQL().createStatement();
		stmt.executeUpdate("INSERT INTO `Banimentos`(`uuid`, `banned_By`, `reason`, `expire`, `ban_time`, `unbanned`) " + "VALUES ('" + ban.getBannedUuid().toString().replace("-", "") + "' , '" + ban.getBannedBy() + "' , '" + ban.getReason() + "' , '" + ban.getDuration() + "', '" + ban.getBanTime() + "', 0);");
		stmt.close();
		Connect.lock.unlock();
	}

	public void mute(final Mute mute) throws Exception {
		if(getPlugin().getServerType() == ServerType.TESTSERVER) {
			throw new Exception("Servidor de Teste");
		}
		mutados.put(mute.getMutedUuid(), mute);
		Connect.lock.lock();
		Statement stmt = getMySQL().createStatement();
		stmt.executeUpdate("INSERT INTO `Mutes`(`uuid`, `muted_By`, `reason`, `expire`, `mute_time`) VALUES ('" + mute.getMutedUuid().toString().replace("-", "") + "', '" + mute.getMutedBy() + "', '" + mute.getReason() + "' , '" + mute.getDuration() + "', '" + mute.getMuteTime() + "');");
		stmt.close();
		Connect.lock.unlock();
	}

	public boolean unmute(final UUID uuid) throws Exception {
		if(getPlugin().getServerType() == ServerType.TESTSERVER) {
			throw new Exception("Servidor de Teste");
		}
		if (!isMuted(uuid))
			return false;
		mutados.remove(uuid);
		Connect.lock.lock();
		Statement stmt = getMySQL().createStatement();
		stmt.executeUpdate("DELETE FROM `Mutes` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
		stmt.close();
		Connect.lock.unlock();
		return true;
	}

	public boolean unban(final UUID uuid) throws Exception {
		if(getPlugin().getServerType() == ServerType.TESTSERVER) {
			throw new Exception("Servidor de Teste");
		}
		if (!isBanned(uuid))
			return false;
		Ban ban = getBan(uuid);
		ban.unban();
		Connect.lock.lock();
		Statement stmt = getMySQL().createStatement();
		stmt.executeUpdate("UPDATE `Banimentos` SET `unbanned`=1 WHERE `uuid`='" + uuid.toString().replace("-", "") + "' and `unbanned`=0;");
		stmt.close();
		Connect.lock.unlock();
		return true;
	}

	public boolean removeTempban(final UUID uuid) throws Exception {
		if(getPlugin().getServerType() == ServerType.TESTSERVER) {
			throw new Exception("Servidor de Teste");
		}
		if (!banimentos.containsKey(uuid))
			return false;
		Ban ban = banimentos.get(uuid);
		ban.unban();
		Connect.lock.lock();
		Statement stmt = getMySQL().createStatement();
		stmt.executeUpdate("DELETE FROM `Banimentos` WHERE `uuid`='" + uuid.toString().replace("-", "") + "' and `expire`!=0;");
		stmt.close();
		Connect.lock.unlock();
		return true;
	}

	public static String getBanMessage(Ban ban) {
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
