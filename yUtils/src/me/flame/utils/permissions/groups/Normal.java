package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

import me.flame.utils.permissions.PermissionManager;

public class Normal extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		switch (PermissionManager.getServerType()) {
		case RAID:
			break;
		default:
			break;
		}
		return permissions;
	}

}
