package me.flame.utils.scoreboard;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import me.flame.utils.Main;
import me.flame.utils.Management;

public class ScoreboardManager extends Management {
	private HashMap<String, Scoreboard> boards;

	public ScoreboardManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		this.boards = new HashMap<>();
	}

	public Scoreboard getPlayerScoreboard(Player player) {
		Scoreboard board = boards.get(player);
		if (board == null) {
			board = getServer().getScoreboardManager().getNewScoreboard();
			boards.put(player.getName(), board);
		}
		return board;
	}

	// TODO Colocar quando o player sair do jogo
	public void removeScoreboard(Player player) {
		if (boards.containsKey(player.getName())) {
			boards.remove(player.getName());
			player.setScoreboard(null);
		}
	}

}
