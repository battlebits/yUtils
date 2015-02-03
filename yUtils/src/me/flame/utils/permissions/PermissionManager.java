package me.flame.utils.permissions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
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
		// TODO Load playerGroups
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
		return playerGroup.ordinal() > group.ordinal();
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
	}

	public void setPlayerGroup(UUID uuid, Group group) {
		if (playerGroups.containsKey(uuid))
			removePlayerGroup(uuid);
		playerGroups.put(uuid, group);
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
			PreparedStatement stmt = getMySQL().prepareStatement("SELECT * FROM `Staff-" + getServerName(getServerType().toString()) + "` WHERE `uuid` = '" + uuid.toString().replace("-", "") + "';");
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

	public static String getServerName(String server) {
		String serverName = server;
		char[] stringArray = serverName.toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		serverName = new String(stringArray);
		return serverName;
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
