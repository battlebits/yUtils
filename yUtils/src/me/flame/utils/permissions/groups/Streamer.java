package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

import me.flame.utils.permissions.PermissionManager;

public class Streamer extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		permissions.add("minecraft.command.*");
		permissions.add("bukkit.command.*");
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
			permissions.add("flame.cmd.say");
			permissions.add("flame.primary");
			permissions.add("flame.evento");
			break;
		case RAID:
			break;
		default:
			break;
		}
		return permissions;
	}

}
