package me.flame.utils.payment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
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
				for (Expire expire : expires.values()) {
					if (expire.getExpire() < System.currentTimeMillis()) {
						getPlugin().getPermissionManager().removePlayer(expire.getUuid());
					}
				}
			}
		}.runTaskLaterAsynchronously(getPlugin(), 20 * 60 * 60);
	}

	@Override
	public void onDisable() {
		expires.clear();
	}

}
