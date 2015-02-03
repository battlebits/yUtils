package me.flame.utils.tagmanager.listeners;

import me.flame.utils.events.AccountLoadEvent;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.tagmanager.TagManager;
import me.flame.utils.tagmanager.enums.Tag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class JoinListener implements Listener {
	private TagManager manager;

	public JoinListener(TagManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(AccountLoadEvent event) {
		Player p = manager.getServer().getPlayer(event.getUUID());
		if (p != null)
			manager.addPlayerTag(p, getPlayerDefaultTag(p));
	}

	private Tag getPlayerDefaultTag(Player p) {
		PermissionManager man = manager.getPlugin().getPermissionManager();
		return Tag.valueOf(man.getPlayerGroup(p).toString());
	}
}
