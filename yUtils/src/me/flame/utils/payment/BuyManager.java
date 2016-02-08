package me.flame.utils.payment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.mysql.Connect;
import me.flame.utils.payment.commands.Givevip;
import me.flame.utils.payment.constructors.Expire;
import me.flame.utils.payment.listeners.JoinListener;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.utils.UUIDFetcher;

import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.MapMaker;

public class BuyManager extends Management {
	public Map<UUID, Expire> expires = new MapMaker().softValues().makeMap();

	public BuyManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new JoinListener(this), getPlugin());
		getPlugin().getCommand("givevip").setExecutor(new Givevip(this));
		new BukkitRunnable() {
			@Override
			public void run() {
				Connect.lock.lock();
				try {
					PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Expires`;");
					ResultSet result = stmt.executeQuery();
					while (result.next()) {
						UUID uuid = UUIDFetcher.getUUIDFromString(result.getString("uuid"));
						Group group = Group.valueOf(result.getString("group").toUpperCase());
						long expire = result.getLong("expire");
						expires.put(uuid, new Expire(uuid, expire, group));
					}
					result.close();
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Erro ao carregar o expire dos players");
				}
				Connect.lock.unlock();
			}
		}.runTaskAsynchronously(getPlugin());
		new BukkitRunnable() {
			@Override
			public void run() {
				Connect.lock.lock();
				try {
					PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Expires`;");
					ResultSet result = stmt.executeQuery();
					expires.clear();
					while (result.next()) {
						UUID uuid = UUIDFetcher.getUUIDFromString(result.getString("uuid"));
						Group group = Group.valueOf(result.getString("group").toUpperCase());
						long expire = result.getLong("expire");
						expires.put(uuid, new Expire(uuid, expire, group));
					}
					result.close();
					stmt.close();
				} catch (Exception e) {
					getLogger().info("Erro ao carregar o expire dos players");
				}
				Connect.lock.unlock();
				Iterator<Expire> iterator = expires.values().iterator();
				while (iterator.hasNext()) {
					Expire expire = iterator.next();
					if (expire.getExpire() < System.currentTimeMillis()) {
						try {
							getPlugin().getPermissionManager().removeRank(expire.getUuid());
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						try {
							removeExpire(expire.getUuid());
						} catch (Exception e) {
							e.printStackTrace();
						}
						iterator.remove();
					}
				}
			}
		}.runTaskLaterAsynchronously(getPlugin(), 20 * 60 * 60);
	}

	public void loadExpire(UUID uuid) throws SQLException {
		Connect.lock.lock();
		PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Expires` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			Group group = Group.valueOf(result.getString("group").toUpperCase());
			long expire = result.getLong("expire");
			expires.put(uuid, new Expire(uuid, expire, group));
		}
		result.close();
		stmt.close();
		Connect.lock.unlock();
	}

	public Expire getExpire(UUID uuid) {
		return expires.get(uuid);
	}

	public void removeExpire(UUID uuid) throws SQLException {
		Connect.lock.lock();
		PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Expires` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			stmt.execute("DELETE FROM `Expires` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
		}
		result.close();
		stmt.close();
		Connect.lock.unlock();
	}

	public void addExpire(UUID uuid, Group grupo, long expireLong) throws SQLException {
		Expire expire;
		if (expires.containsKey(uuid)) {
			expire = expires.get(uuid);
			expire.setGroup(grupo);
			expire.addLong(expireLong);
		} else
			expire = new Expire(uuid, expireLong, grupo);
		expires.put(uuid, expire);
		Connect.lock.lock();
		PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Expires` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			stmt.execute("UPDATE `Expires` SET `group`='" + expire.getGroup().toString().toLowerCase() + "', `expire`=" + expireLong + "  WHERE uuid='" + uuid.toString().replace("-", "") + "';");
		} else {
			stmt.execute("INSERT INTO `Expires`(`uuid`, `expire`, `group`) VALUES ('" + uuid.toString().replace("-", "") + "'," + expire.getExpire() + " ,'" + expire.getGroup().toString().toLowerCase() + "');");
		}
		result.close();
		stmt.close();
		Connect.lock.unlock();
	}

	@Override
	public void onDisable() {
		expires.clear();
	}

}
