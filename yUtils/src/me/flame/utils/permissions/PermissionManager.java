package me.flame.utils.permissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.permissions.listeners.LoginListener;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class PermissionManager extends Management {
	private HashMap<UUID, Group> playerGroups;

	public PermissionManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new LoginListener(getPlugin()), getPlugin());
		// Load playerGroups
		playerGroups = new HashMap<>();
	}

	public boolean isGroup(Player player, Group group) {
		if (!playerGroups.containsKey(player.getUniqueId()))
			return false;
		return playerGroups.get(player.getUniqueId()) == group;
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
}
