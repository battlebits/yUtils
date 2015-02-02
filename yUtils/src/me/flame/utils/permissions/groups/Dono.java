package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

import me.flame.utils.permissions.PermissionManager;

public class Dono extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		permissions.add("*");
		switch (PermissionManager.getServerType()) {
		case HUNGERGAMES:
			break;
		case SKYWARS:
			break;
		case LOBBY:
			break;
		case PVP:
			break;
		default:
			break;
		}
		return permissions;
	}

}
