package me.flame.utils.scoreboard;

import java.util.HashMap;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.scoreboard.listeners.QuitListener;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager extends Management {
	private HashMap<UUID, Scoreboard> playerBoards;

	public ScoreboardManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new QuitListener(this), getPlugin());
		playerBoards = new HashMap<>();
	}

	public Scoreboard getPlayerScoreboard(Player player) {
		Scoreboard board = playerBoards.get(player.getUniqueId());
		if (board == null) {
			board = getServer().getScoreboardManager().getNewScoreboard();
		}
		player.setScoreboard(board);
		return board;
	}

	public void removeScoreboard(Player player) {
		playerBoards.remove(player.getUniqueId());
	}

	@Override
	public void onDisable() {
		for (Player player : getServer().getOnlinePlayers()) {
			removeScoreboard(player);
		}
	}

}
