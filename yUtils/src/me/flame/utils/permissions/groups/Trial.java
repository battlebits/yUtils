package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

import me.flame.utils.permissions.PermissionManager;

public class Trial extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		permissions.add("minecraft.command.tp");
		permissions.add("bukkit.command.teleport");
		permissions.add("knohax.forcecheck");
		permissions.add("nohax.viewalerts");
		switch (PermissionManager.getServerType()) {
		case HUNGERGAMES:
			break;
		case SKYWARS:
			break;
		case LOBBY:
			permissions.add("battle.vip");
			break;
		case BATTLECRAFT:
			permissions.add("flame.mod");
			break;
		case RAID:
			break;
		default:
			break;
		}
		return permissions;
	}

}
