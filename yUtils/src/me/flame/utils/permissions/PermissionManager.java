package me.flame.utils.permissions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.mysql.Connect;
import me.flame.utils.permissions.commands.GiveYoutuber;
import me.flame.utils.permissions.commands.GroupSet;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.permissions.enums.ServerType;
import me.flame.utils.permissions.injector.PermissionMatcher;
import me.flame.utils.permissions.injector.RegExpMatcher;
import me.flame.utils.permissions.injector.regexperms.RegexPermissions;
import me.flame.utils.permissions.listeners.LoginListener;

import org.bukkit.entity.Player;

public class PermissionManager extends Management {
	private HashMap<UUID, Group> playerGroups;
	private static ServerType type = ServerType.NONE;
	private RegexPermissions regexPerms;
	protected PermissionMatcher matcher = new RegExpMatcher();
	protected LoginListener superms;

	public PermissionManager(Main main, ServerType typea) {
		super(main);
		type = typea;
	}

	@Override
	public void onEnable() {
		superms = new LoginListener(getPlugin());
		getServer().getPluginManager().registerEvents(superms, getPlugin());
		getPlugin().getCommand("giveyoutuber").setExecutor(new GiveYoutuber(this));
		getPlugin().getCommand("groupset").setExecutor(new GroupSet(this));
		regexPerms = new RegexPermissions(this);
		playerGroups = new HashMap<>();
		try {
			Connect.lock.lock();
			PreparedStatement stmt = null;
			ResultSet result = null;
			for (Player p : getServer().getOnlinePlayers()) {
				UUID uuid = p.getUniqueId();
				stmt = getMySQL().prepareStatement("SELECT * FROM `Staff-" + getServerType().toString() + "` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
				result = stmt.executeQuery();
				if (result.next()) {
					Group grupo = Group.valueOf(result.getString("rank").toUpperCase());
					setPlayerGroup(uuid, grupo);
					result.close();
					stmt.close();
				} else {
					result.close();
					stmt.close();
					stmt = getMySQL().prepareStatement("SELECT * FROM `Ranks` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
					result = stmt.executeQuery();
					if (result.next()) {
						Group grupo = Group.valueOf(result.getString("rank").toUpperCase());
						setPlayerGroup(uuid, grupo);
					}
				}
			}
			if (result != null)
				result.close();
			if (stmt != null)
				stmt.close();
			Connect.lock.unlock();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isGroup(Player player, Group group) {
		if (!playerGroups.containsKey(player.getUniqueId()))
			return false;
		return playerGroups.get(player.getUniqueId()) == group;
	}

	public boolean hasGroupPermission(Player player, Group group) {
		return hasGroupPermission(player.getUniqueId(), group);
	}

	public boolean hasGroupPermission(UUID uuid, Group group) {
		if (!playerGroups.containsKey(uuid))
			return false;
		Group playerGroup = playerGroups.get(uuid);
		return playerGroup.ordinal() >= group.ordinal();
	}

	public static ServerType getServerType() {
		return type;
	}

	public RegexPermissions getRegexPerms() {
		return regexPerms;
	}

	public PermissionMatcher getPermissionMatcher() {
		return this.matcher;
	}

	public void setPlayerGroup(Player player, Group group) {
		UUID uuid = player.getUniqueId();
		playerGroups.put(uuid, group);
	}

	public void savePlayerGroup(UUID uuid, Group group) throws SQLException {
		Connect.lock.lock();
		if (group.ordinal() >= Group.HELPER.ordinal()) {
			PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Staff-" + getServerType().toString() + "` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				stmt.execute("UPDATE `Staff-" + getServerType().toString() + "` SET rank='" + group.toString().toLowerCase() + "' WHERE uuid='" + uuid.toString().replace("-", "") + "';");
			} else {
				stmt.execute("INSERT INTO `Staff-" + getServerType().toString() + "`(`uuid`, `rank`) VALUES ('" + uuid.toString().replace("-", "") + "', '" + group.toString().toLowerCase() + "');");
			}
			result.close();
			stmt.close();
		} else {
			if (getPlugin().getServerType() != ServerType.TESTSERVER) {
				PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Ranks` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
				ResultSet result = stmt.executeQuery();
				if (result.next()) {
					stmt.execute("UPDATE `Ranks` SET `rank`='" + group.toString().toLowerCase() + "' WHERE uuid='" + uuid.toString().replace("-", "") + "';");
				} else {
					stmt.execute("INSERT INTO `Ranks`(`uuid`, `rank`) VALUES ('" + uuid.toString().replace("-", "") + "', '" + group.toString().toLowerCase() + "');");
				}
				result.close();
				stmt.close();
			}
		}
		Connect.lock.unlock();
	}

	public void removePlayer(UUID uuid) throws SQLException {
		Connect.lock.lock();
		PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Staff-" + getServerType().toString() + "` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			stmt.execute("DELETE FROM `Staff-" + getServerType().toString() + "` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
		}
		result.close();
		stmt.close();
		if (getPlugin().getServerType() != ServerType.TESTSERVER) {
			stmt = getMySQL().prepareStatement("SELECT * FROM `Ranks` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
			result = stmt.executeQuery();
			if (result.next()) {
				stmt.execute("DELETE FROM `Ranks` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
			}
		}
		result.close();
		stmt.close();
		Connect.lock.unlock();
	}

	public void setPlayerGroup(UUID uuid, Group group) {
		playerGroups.put(uuid, group);
	}

	public void removePlayerGroup(UUID uuid) {
		playerGroups.remove(uuid);
	}

	public Group getPlayerGroup(Player player) {
		if (!playerGroups.containsKey(player.getUniqueId()))
			return Group.NORMAL;
		return playerGroups.get(player.getUniqueId());
	}

	public Group getPlayerGroup(UUID uuid) {
		if (!playerGroups.containsKey(uuid))
			return Group.NORMAL;
		return playerGroups.get(uuid);
	}

	public void loadPlayerGroup(UUID uuid) throws SQLException {
		Connect.lock.lock();
		PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Staff-" + getServerType().toString() + "` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			Group grupo = Group.valueOf(result.getString("rank").toUpperCase());
			setPlayerGroup(uuid, grupo);
			result.close();
			stmt.close();
		} else {
			result.close();
			stmt.close();
			stmt = getMySQL().prepareStatement("SELECT * FROM `Ranks` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
			result = stmt.executeQuery();
			if (result.next()) {
				Group grupo = Group.valueOf(result.getString("rank").toUpperCase());
				setPlayerGroup(uuid, grupo);
			}
			result.close();
			stmt.close();
		}
		Connect.lock.unlock();
	}

	@Override
	public void onDisable() {
		if (this.regexPerms != null) {
			this.regexPerms.onDisable();
			this.regexPerms = null;
		}
		if (this.superms != null) {
			this.superms.onDisable();
			this.superms = null;
		}
		if (playerGroups != null)
			playerGroups.clear();
	}
}
