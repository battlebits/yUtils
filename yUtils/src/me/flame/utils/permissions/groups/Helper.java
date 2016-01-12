package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

import me.flame.utils.permissions.PermissionManager;

public class Helper extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		switch (PermissionManager.getServerType()) {
		case HUNGERGAMES:
			break;
		case SKYWARS:
			break;
		case LOBBY:
			permissions.add("battle.vip");
			break;
		case BATTLECRAFT:
			permissions.add("flame.vip.pro");
			break;
		case RAID:
			break;
		default:
			break;
		}
		return permissions;
	}

}