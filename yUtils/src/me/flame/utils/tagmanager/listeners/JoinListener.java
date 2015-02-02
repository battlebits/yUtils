package me.flame.utils.tagmanager.listeners;

import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.tagmanager.TagManager;
import me.flame.utils.tagmanager.enums.Tag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
	private TagManager manager;

	public JoinListener(TagManager manager) {
		this.manager = manager;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		manager.addPlayerTag(p, getPlayerDefaultTag(p));
	}

	private Tag getPlayerDefaultTag(Player p) {
		PermissionManager man = manager.getPlugin().getPermissionManager();
		if (man.isGroup(p, Group.DONO)) {
			return Tag.DONO;
		} else if (man.isGroup(p, Group.ADMIN)) {
			return Tag.ADMIN;
		} else if (man.isGroup(p, Group.MOD)) {
			return Tag.MOD;
		} else if (man.isGroup(p, Group.TRIAL)) {
			return Tag.TRIAL;
		} else if (man.isGroup(p, Group.HELPER)) {
			return Tag.HELPER;
		} else if (man.isGroup(p, Group.STAFF)) {
			return Tag.STAFF;
		} else if (man.isGroup(p, Group.YOUTUBER)) {
			return Tag.YOUTUBER;
		} else if (man.isGroup(p, Group.ULTIMATE)) {
			return Tag.ULTIMATE;
		} else if (man.isGroup(p, Group.PREMIUM)) {
			return Tag.PREMIUM;
		} else if (man.isGroup(p, Group.LIGHT)) {
			return Tag.LIGHT;
		} else {
			return Tag.NORMAL;
		}
	}
}
