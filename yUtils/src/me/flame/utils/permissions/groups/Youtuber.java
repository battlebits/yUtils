package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

import me.flame.utils.permissions.PermissionManager;

public class Youtuber extends MainGroup {

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
			break;
		case RAID:
			permissions.add("inception.youtuber");
			permissions.add("inception.ultimate");
			permissions.add("inception.premium");
			permissions.add("inception.light");
			permissions.add("inception.normal");
			break;
		default:
			break;
		}
		return permissions;
	}

}