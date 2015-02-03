package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

import me.flame.utils.permissions.PermissionManager;

public class Trial extends MainGroup {

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
		case PVP:
			permissions.add("minecraft.command.tp");
			permissions.add("knohax.forcecheck");
			permissions.add("nohax.viewalerts");
			permissions.add("flame.mod");
			permissions.add("bm.ban");
			permissions.add("bm.tempban");
			permissions.add("bm.kick");
			permissions.add("bm.tempmute");
			break;
		default:
			break;
		}
		return permissions;
	}

}
