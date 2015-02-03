package me.flame.utils.scoreboard;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.scoreboard.listeners.QuitListener;

public class ScoreboardManager extends Management {
	private HashMap<String, Scoreboard> boards;

	public ScoreboardManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		this.boards = new HashMap<>();
		getServer().getPluginManager().registerEvents(new QuitListener(this), getPlugin());
	}

	public Scoreboard getPlayerScoreboard(Player player) {
		Scoreboard board = boards.get(player);
		if (board == null) {
			board = getServer().getScoreboardManager().getNewScoreboard();
			boards.put(player.getName(), board);
			player.setScoreboard(boards.get(player.getName()));
		}
		return board;
	}

	public void removeScoreboard(Player player) {
		if (boards.containsKey(player.getName())) {
			boards.remove(player.getName());
		}
	}

	@Override
	public void onDisable() {

	}

}
