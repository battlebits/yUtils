package me.flame.utils.permissions.groups;

import java.util.ArrayList;
import java.util.List;

import me.flame.utils.permissions.PermissionManager;

public class Moderator extends MainGroup {

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
			permissions.add("flame.cmd.say");
			permissions.add("knohax.forcecheck");
			permissions.add("nohax.viewalerts");
			permissions.add("flame.primary");
			permissions.add("bm.ban");
			permissions.add("bm.tempban");
			permissions.add("bm.kick");
			permissions.add("bm.mute");
			permissions.add("bm.tempmute");
			permissions.add("bm.unmute");
			permissions.add("bm.tempmuteoffline");
			permissions.add("bm.tempbanoffline");
			break;
		default:
			break;
		}
		return permissions;
	}

}
