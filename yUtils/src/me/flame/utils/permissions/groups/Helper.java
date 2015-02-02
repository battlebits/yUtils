package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

public class Helper extends MainGroup {

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		permissions.add("");
		return permissions;
	}

}