package me.flame.utils.permissions;

import java.util.List;
import java.util.Map.Entry;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.permissions.listeners.LoginListener;

import org.bukkit.permissions.Permission;

public class PermissionManager extends Management {

	public PermissionManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new LoginListener(getPlugin()), getPlugin());
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
