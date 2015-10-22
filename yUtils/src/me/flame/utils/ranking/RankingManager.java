package me.flame.utils.ranking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.ranking.constructors.Account;
import me.flame.utils.ranking.listeners.RankingListener;

import org.bukkit.entity.Player;

public class RankingManager extends Management {

	private HashMap<UUID, Account> accounts;

	public RankingManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		accounts = new HashMap<>();
		getPlugin().getServer().getPluginManager().registerEvents(new RankingListener(this), getPlugin());
		try {
			PreparedStatement stmt = null;
			ResultSet result = null;
			for (Player p : getServer().getOnlinePlayers()) {
				UUID uuid = p.getUniqueId();
				stmt = getMySQL().prepareStatement("SELECT * FROM `Accounts` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
				result = stmt.executeQuery();
				if (result.next()) {
					int money = result.getInt("money");
					int fichas = result.getInt("fichas");
					int xp = result.getInt("xp");
					accounts.put(uuid, new Account(uuid, xp, money, fichas));
				}
			}
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadAccount(final UUID uuid) throws SQLException {
		PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Accounts` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			int money = result.getInt("money");
			int fichas = result.getInt("fichas");
			int xp = result.getInt("xp");
			accounts.put(uuid, new Account(uuid, xp, money, fichas));
		}
		result.close();
		stmt.close();
	}

	public void loadAccountNoRunnable(UUID uuid) throws SQLException {
		PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Accounts` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			int money = result.getInt("money");
			int fichas = result.getInt("fichas");
			int xp = result.getInt("xp");
			accounts.put(uuid, new Account(uuid, xp, money, fichas));
		}
		result.close();
		stmt.close();
	}

	public Account getAccount(Player player) {
		return getAccount(player.getUniqueId());
	}

	public boolean containsAccount(UUID uuid) {
		return accounts.containsKey(uuid);
	}

	public Account getAccount(UUID uuid) {
		if (accounts.containsKey(uuid))
			return accounts.get(uuid);
		Account account = new Account(uuid);
		accounts.put(uuid, account);
		return account;
	}

	public void removeAccount(final UUID uuid) throws SQLException {
		final Account account = accounts.get(uuid);
		if (account != null)
			if (account.getFichas() != 0 || account.getMoney() != 0 || account.getXp() != 0) {
				PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Accounts` WHERE `uuid`='" + account.getUuid().toString().replace("-", "") + "';");
				ResultSet result = stmt.executeQuery();
				if (result.next()) {
					stmt.execute("UPDATE `Accounts` SET fichas=" + account.getFichas() + ", money=" + account.getMoney() + ", xp=" + account.getXp() + " WHERE uuid='" + account.getUuid().toString().replace("-", "") + "';");
				} else {
					stmt.execute("INSERT INTO `Accounts`(`uuid`, `xp`, `fichas`, `money`) VALUES ('" + account.getUuid().toString().replace("-", "") + "', " + account.getXp() + ", " + account.getFichas() + ", " + account.getMoney() + ");");
				}
				result.close();
				stmt.close();
			}
		accounts.remove(uuid);
	}

	@Override
	public void onDisable() {
		try {
			PreparedStatement stmt = null;
			ResultSet result = null;
			for (Account account : accounts.values()) {
				if (account.getFichas() != 0 || account.getMoney() != 0 || account.getXp() != 0) {
					stmt = getMySQL().prepareStatement("SELECT * FROM `Accounts` WHERE `uuid`='" + account.getUuid().toString().replace("-", "") + "';");
					result = stmt.executeQuery();
					if (result.next()) {
						stmt.execute("UPDATE `Accounts` SET fichas=" + account.getFichas() + ", money=" + account.getMoney() + ", xp=" + account.getXp() + " WHERE uuid='" + account.getUuid().toString().replace("-", "") + "';");
					} else {
						stmt.execute("INSERT INTO `Accounts`(`uuid`, `xp`, `fichas`, `money`) VALUES ('" + account.getUuid().toString().replace("-", "") + "', " + account.getXp() + ", " + account.getFichas() + ", " + account.getMoney() + ");");
					}
				}
			}
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		accounts.clear();
		accounts = null;
	}

}
