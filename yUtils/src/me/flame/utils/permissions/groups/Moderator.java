package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

import me.flame.utils.permissions.PermissionManager;

public class Moderator extends MainGroup{

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		permissions.add("minecraft.*");
		permissions.add("minecraft.command.*");
		permissions.add("minecraft.command.tp");
		switch(PermissionManager.getServerType()) {
		case HUNGERGAMES:
			break;
		case SKYWARS:
			break;
		case LOBBY:
			break;
		case PVP:
			permissions.add("flame.admin");
			break;
		default:
			break;
		}
		return permissions;
	}

}
