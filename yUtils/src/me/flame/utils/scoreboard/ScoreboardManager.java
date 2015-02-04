package me.flame.utils.scoreboard;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.scoreboard.listeners.QuitListener;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager extends Management {

	public ScoreboardManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new QuitListener(this), getPlugin());
	}

	public Scoreboard getPlayerScoreboard(Player player) {
		Scoreboard board = player.getScoreboard();
		if (board == null) {
			board = getServer().getScoreboardManager().getNewScoreboard();
			player.setScoreboard(board);
		}
		return board;
	}

	public void removeScoreboard(Player player) {
		player.setScoreboard(getServer().getScoreboardManager().getNewScoreboard());
	}

	@Override
	public void onDisable() {
		for (Player player : getServer().getOnlinePlayers()) {
			removeScoreboard(player);
		}
	}

}
