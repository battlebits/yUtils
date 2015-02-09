package me.flame.utils.payment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.payment.constructors.Expire;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.utils.UUIDFetcher;

public class BuyManager extends Management {
	private HashMap<UUID, Expire> expires;

	public BuyManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		expires = new HashMap<>();
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Expire`;");
					ResultSet result = stmt.executeQuery();
					while (result.next()) {
						UUID uuid = UUIDFetcher.getUUID(result.getString("uuid"));
						Group group = Group.valueOf(result.getString("group").toUpperCase());
						long expire = result.getLong("expire");
						expires.put(uuid, new Expire(uuid, expire, group));
					}
					result.close();
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Erro ao carregar o expire dos players");
				}
			}
		}.runTaskAsynchronously(getPlugin());
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Expire`;");
					ResultSet result = stmt.executeQuery();
					expires.clear();
					while (result.next()) {
						UUID uuid = UUIDFetcher.getUUID(result.getString("uuid"));
						Group group = Group.valueOf(result.getString("group").toUpperCase());
						long expire = result.getLong("expire");
						expires.put(uuid, new Expire(uuid, expire, group));
					}
					result.close();
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Erro ao carregar o expire dos players");
				}
				Iterator<Expire> iterator = expires.values().iterator();
				while (iterator.hasNext()) {
					Expire expire = iterator.next();
					if (expire.getExpire() < System.currentTimeMillis()) {
						getPlugin().getPermissionManager().removePlayer(expire.getUuid());
						removeExpire(expire.getUuid());
						iterator.remove();
					}
				}
			}
		}.runTaskLaterAsynchronously(getPlugin(), 20 * 60 * 60);
	}

	public void removeExpire(UUID uuid) {
		try {
			PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Expire` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				stmt.execute("DELETE FROM `Expire` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
			}
			result.close();
			stmt.close();
		} catch (Exception e) {
		}
	}

	public void addExpire(UUID uuid, Group grupo, long expireLong) {
		Expire expire;
		if (expires.containsKey(uuid)) {
			expire = expires.get(uuid);
			expire.setGroup(grupo);
			expire.addLong(expireLong);
		} else
			expire = new Expire(uuid, expireLong, grupo);
		expires.put(uuid, expire);
		try {
			PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Expire` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				stmt.execute("UPDATE `Expire` SET `group`='" + expire.getGroup().toString().toLowerCase() + "', `expire`=" + expireLong + "  WHERE uuid='" + uuid.toString().replace("-", "") + "';");
			} else {
				stmt.execute("INSERT INTO `Expire`(`uuid`, `expire`, `group`) VALUES ('" + uuid.toString().replace("-", "") + "'," + expire.getExpire() + " ,'" + expire.getGroup().toString().toLowerCase() + "');");
			}
			result.close();
			stmt.close();
		} catch (Exception e) {
			getLogger().info("Erro ao inserir Expire vip");
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		expires.clear();
	}

}
