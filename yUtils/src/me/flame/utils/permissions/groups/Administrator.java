package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

import me.flame.utils.permissions.PermissionManager;

public class Administrator extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		switch (PermissionManager.getServerType()) {
		case HUNGERGAMES:
			break;
		case SKYWARS:
			break;
		case LOBBY:
			break;
		case BATTLECRAFT:
			permissions.add("flame.cmd.say");
			permissions.add("flame.adminless");
			permissions.add("minecraft.command.tp");
			permissions.add("nohax.viewalerts");
			permissions.add("bm.*");
			permissions.add("permission.*");
			break;
		default:
			break;
		}
		return permissions;
	}

}
