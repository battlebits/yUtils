package me.flame.utils.banmanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.banmanager.commands.Kick;
import me.flame.utils.banmanager.commands.Unban;
import me.flame.utils.banmanager.commands.Unmute;
import me.flame.utils.banmanager.constructors.Ban;
import me.flame.utils.banmanager.constructors.Mute;
import me.flame.utils.banmanager.listeners.LoginListener;
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
		getServer().getPluginManager().registerEvents(new LoginListener(this), getPlugin());
		getPlugin().getCommand("kick").setExecutor(new Kick(this));
		getPlugin().getCommand("ban").setExecutor(new me.flame.utils.banmanager.commands.Ban(this));
		getPlugin().getCommand("unban").setExecutor(new Unban(this));
		getPlugin().getCommand("ban").setExecutor(new me.flame.utils.banmanager.commands.Mute(this));
		getPlugin().getCommand("unmute").setExecutor(new Unmute(this));
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
						long expire = result.getLong("expire");
						long ban_time = result.getLong("ban_time");
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
						long expire = result.getLong("expire");
						long mute_time = result.getLong("mute_time");
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
					stmt.executeUpdate("INSERT INTO `Banimentos`(`uuid`, `banned_By`, `reason`, `expire`, `ban_time`, `unbanned`) " + "VALUES ('" + ban.getBannedUuid().toString().replace("-", "") + "' , '" + ban.getBannedBy() + "' , '" + ban.getReason() + "' , " + ban.getDuration() + ", " + ban.getBanTime() + " , 0);");
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Nao foi possivel banir " + ban.getBannedUuid().toString());
					e.printStackTrace();
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
					stmt.executeUpdate("INSERT INTO `Mutes`(`uuid`, `muted_By`, `reason`, `expire`, `mute_time`) VALUES ('" + mute.getMutedUuid() + "', '" + mute.getMutedBy() + "', '" + mute.getReason() + "' , " + mute.getDuration() + ", NOW());");
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Nao foi possivel mutar " + mute.getMutedUuid().toString());
					e.printStackTrace();
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
					stmt.executeUpdate("DELETE FROM `Mutes` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Nao foi possivel desmutar " + uuid);
					e.printStackTrace();
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
					stmt.executeUpdate("UPDATE `Banimentos` SET `unbanned`=1 WHERE `uuid`='" + uuid.toString().replace("-", "") + "', `unbanned`=0;");
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Nao foi possivel desbanir " + uuid);
					e.printStackTrace();
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
