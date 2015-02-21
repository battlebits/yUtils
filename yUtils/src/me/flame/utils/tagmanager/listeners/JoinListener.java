package me.flame.utils.tagmanager.listeners;

import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.tagmanager.TagManager;
import me.flame.utils.tagmanager.enums.Tag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {
	private TagManager manager;

	public JoinListener(TagManager manager) {
		this.manager = manager;
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : manager.getServer().getOnlinePlayers()) {
					manager.addPlayerTag(player, getPlayerDefaultTag(player));
				}
			}
		}.runTaskLater(manager.getPlugin(), 11);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		manager.addPlayerTag(event.getPlayer(), getPlayerDefaultTag(event.getPlayer()));
	}

	private Tag getPlayerDefaultTag(Player p) {
		PermissionManager man = manager.getPlugin().getPermissionManager();
		return Tag.valueOf(man.getPlayerGroup(p).toString());
	}
}
