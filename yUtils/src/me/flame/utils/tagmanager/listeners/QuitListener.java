package me.flame.utils.tagmanager.listeners;

import me.flame.utils.tagmanager.TagManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
	private TagManager manager;

	public QuitListener(TagManager manager) {
		this.manager = manager;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		handleQuit(event.getPlayer());
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		handleQuit(event.getPlayer());
	}

	private void handleQuit(Player player) {
		manager.removePlayerTag(player);
	}

}
