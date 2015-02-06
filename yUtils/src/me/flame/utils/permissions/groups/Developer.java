package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

public class Developer extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>(new Ultimate().getPermissions());
		return permissions;
	}

}
