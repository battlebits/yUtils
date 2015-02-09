package me.flame.utils.permissions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.events.AccountLoadEvent;
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
	}

	public boolean isGroup(Player player, Group group) {
		if (!playerGroups.containsKey(player.getUniqueId()))
			return false;
		return playerGroups.get(player.getUniqueId()) == group;
	}

	public boolean hasGroupPermission(Player player, Group group) {
		if (!playerGroups.containsKey(player.getUniqueId()))
			return false;
		Group playerGroup = playerGroups.get(player.getUniqueId());
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
		if (playerGroups.containsKey(uuid))
			removePlayerGroup(player);
		playerGroups.put(uuid, group);
		getServer().getPluginManager().callEvent(new AccountLoadEvent(player.getUniqueId(), group));
	}

	public void savePlayerGroup(UUID uuid, Group group) {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removePlayer(UUID uuid) {
		try {
			PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Staff-" + getServerType().toString() + "` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				stmt.execute("DELETE FROM `Staff-" + getServerType().toString() + "` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
			}
			stmt = getMySQL().prepareStatement("SELECT * FROM `Ranks` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
			result = stmt.executeQuery();
			if (result.next()) {
				stmt.execute("DELETE FROM `Ranks` WHERE `uuid`='" + uuid.toString().replace("-", "") + "';");
			}
			result.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPlayerGroup(UUID uuid, Group group) {
		if (playerGroups.containsKey(uuid))
			removePlayerGroup(uuid);
		playerGroups.put(uuid, group);
		getServer().getPluginManager().callEvent(new AccountLoadEvent(uuid, group));
	}

	public void removePlayerGroup(Player player) {
		playerGroups.remove(player.getUniqueId());
	}

	public void removePlayerGroup(UUID uuid) {
		playerGroups.remove(uuid);
	}

	public Group getPlayerGroup(Player player) {
		return playerGroups.get(player.getUniqueId());
	}

	public Group getPlayerGroup(UUID uuid) {
		return playerGroups.get(uuid);
	}

	public void loadPlayerGroup(UUID uuid) {
		try {
			PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Staff-" + getServerType().toString() + "` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				Group grupo = Group.valueOf(result.getString("rank").toUpperCase());
				System.out.println(grupo.toString());
				setPlayerGroup(uuid, grupo);
			} else {
				stmt = getMySQL().prepareStatement("SELECT * FROM `Ranks` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
				result = stmt.executeQuery();
				if (result.next()) {
					Group grupo = Group.valueOf(result.getString("rank").toUpperCase());
					System.out.println(grupo.toString());
					setPlayerGroup(uuid, grupo);
				} else {
					setPlayerGroup(uuid, Group.NORMAL);
				}
			}
			result.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	}
}
