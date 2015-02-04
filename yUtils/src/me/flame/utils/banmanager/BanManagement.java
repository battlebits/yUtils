package me.flame.utils.banmanager;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.banmanager.constructors.Ban;
import me.flame.utils.banmanager.constructors.Mute;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.entity.Player;

public class BanManagement extends Management {
	private HashMap<UUID, Ban> banimentos;
	private HashMap<UUID, Mute> mutados;

	public BanManagement(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		this.banimentos = new HashMap<>();
		this.mutados = new HashMap<>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Banimentos`;");
					ResultSet result = stmt.executeQuery();
					while (result.next()) {
						UUID uuid = UUIDFetcher.getUUID(result.getString("uuid"));
						String bannedBy = result.getString("banned_By");
						String reason = result.getString("reason");
						boolean unbanned = result.getInt("unbanned") == 1;
						Date expire = result.getDate("expire");
						Date ban_time = result.getDate("ban_time");
						Ban ban = banimentos.get(uuid);
						if (ban == null)
							ban = new Ban(uuid, bannedBy, reason, ban_time, expire, unbanned);
						else
							ban.setNewBan(bannedBy, reason, ban_time, expire, unbanned);
						banimentos.put(uuid, ban);
					}
					stmt = getMySQL().prepareStatement("SELECT * FROM `Mutes`;");
					result = stmt.executeQuery();
					while (result.next()) {
						UUID uuid = UUIDFetcher.getUUID(result.getString("uuid"));
						String mutedBy = result.getString("muted_By");
						String reason = result.getString("reason");
						Date expire = result.getDate("expire");
						Date mute_time = result.getDate("mute_time");
						Mute mute = new Mute(uuid, mutedBy, reason, mute_time, expire);
						mutados.put(uuid, mute);
					}
					result.close();
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Nao foi possivel carregar banimentos e mutes");
				}
			}
		}).start();

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

	public void ban(Ban ban) {
		banimentos.put(ban.getBannedUuid(), ban);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Statement stmt = getMySQL().createStatement();
					stmt.executeQuery("INSERT INTO `Banimentos`(`uuid`, `banned_By`, `reason`, `expire`, `ban_time`, `unbaned`) " + "VALUES ('" + ban.getBannedUuid().toString().replace("-", "") + "' , '" + ban.getBannedBy() + "' , '" + ban.getReason() + "' , " + ban.getBanTime() + ", NOW() , 0);");
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Nao foi possivel banir " + ban.getBannedUuid().toString());
				}
			}
		}).start();
	}

	public void mute(Mute mute) {
		mutados.put(mute.getMutedUuid(), mute);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Statement stmt = getMySQL().createStatement();
					stmt.executeQuery("INSERT INTO `Mutes`(`uuid`, `muted_By`, `reason`, `expire`, `mute_time`) VALUES ('" + mute.getMutedUuid() + "', '" + mute.getMutedBy() + "', '" + mute.getReason() + "' , " + mute.getDuration() + ", NOW());");
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Nao foi possivel banir " + mute.getMutedUuid().toString());
				}
			}
		}).start();
	}

	public boolean unmute(UUID uuid) {
		if (isMuted(uuid))
			return false;
		mutados.remove(uuid);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Statement stmt = getMySQL().createStatement();
					stmt.executeQuery("DELETE FROM `Mutes` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Nao foi possivel desbanir " + uuid);
				}
			}
		}).start();
		return true;
	}

	public boolean unban(UUID uuid) {
		if (isBanned(uuid))
			return false;
		Ban ban = getBan(uuid);
		ban.unban();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Statement stmt = getMySQL().createStatement();
					stmt.executeQuery("UPDATE `Banimentos` SET `unbanned`=1 WHERE `uuid`='" + uuid.toString().replace("-", "") + "', `unbanned`=0;");
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Nao foi possivel desbanir " + uuid);
				}
			}
		}).start();
		return true;
	}

	@Override
	public void onDisable() {
		banimentos.clear();
	}
}
