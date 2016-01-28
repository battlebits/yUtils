package me.flame.utils.tagmanager.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.flame.utils.tagmanager.TagManager;

public class JoinListener implements Listener {
	private TagManager manager;

	public JoinListener(TagManager manager) {
		this.manager = manager;
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : JoinListener.this.manager.getServer().getOnlinePlayers()) {
					JoinListener.this.manager.addPlayerTag(player, TagManager.getPlayerDefaultTag(player));
				}
			}
		}.runTaskLater(manager.getPlugin(), 11);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		manager.addPlayerTag(p, TagManager.getPlayerDefaultTag(p));
	}

}
