package me.flame.utils.scoreboard.listeners;

import me.flame.utils.scoreboard.ScoreboardManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;

public class QuitListener implements Listener {
	private ScoreboardManager manager;

	public QuitListener(ScoreboardManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
		handleQuit(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKick(PlayerKickEvent event) {
		handleQuit(event.getPlayer());
	}

	private void handleQuit(Player player) {
		manager.removeScoreboard(player);
		Scoreboard board = manager.getPlayerScoreboard(player);
		for (Player online : manager.getServer().getOnlinePlayers()) {
			if (board.getPlayerTeam(online) != null)
				board.getPlayerTeam(online).removePlayer(online);
		}
	}
}