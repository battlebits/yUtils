package me.flame.utils.permissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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
import org.bukkit.permissions.Permission;

public class PermissionManager extends Management {
	private HashMap<UUID, Group> playerGroups;
	private static ServerType type = ServerType.NONE;
	private RegexPermissions regexPerms;
	protected PermissionMatcher matcher = new RegExpMatcher();

	public PermissionManager(Main main, ServerType typea) {
		super(main);
		type = typea;
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new LoginListener(getPlugin()), getPlugin());
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

	public void addChildren(Main main, String name, List<String> permList) {
		Permission perm = main.getServer().getPluginManager().getPermission(name);
		if (perm == null)
			return;

		if (!permList.contains(perm.getName())) {
			permList.add(perm.getName());
		}
		for (Entry<String, Boolean> child : perm.getChildren().entrySet()) {
			if (!permList.contains(child.getKey())) {
				addChildren(main, child.getKey(), permList);
			}
		}
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
}
