package me.flame.utils.tagmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.tagmanager.enums.Tag;
import me.flame.utils.tagmanager.listeners.JoinListener;
import me.flame.utils.tagmanager.listeners.QuitListener;

public class TagManager extends Management {
	private HashMap<Tag, List<Player>> tags;

	public TagManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		tags = new HashMap<>();
		getServer().getPluginManager().registerEvents(new JoinListener(this), getPlugin());
		getServer().getPluginManager().registerEvents(new QuitListener(this), getPlugin());
	}

	public void addPlayerTag(Player player, Tag tag) {
		Scoreboard board = getPlugin().getScoreboardManager().getPlayerScoreboard(player);
		for (Tag teamTag : Tag.values()) {
			Team team = board.registerNewTeam(teamTag.getTeamName());
			team.setPrefix(teamTag.getPrefix());
			team.setSuffix(ChatColor.RESET + "");
		}
		List<Player> playerList = tags.get(tag);
		if (playerList == null) {
			playerList = new ArrayList<>();
			playerList.add(player);
			tags.put(tag, playerList);
		} else {
			playerList.add(player);
		}
		for (Entry<Tag, List<Player>> entry : tags.entrySet()) {
			Tag tagteam = entry.getKey();
			List<Player> players = entry.getValue();
			Team team = board.getTeam(tagteam.getTeamName());
			for (Player participante : players) {
				if (team != null)
					team.addPlayer(participante);
				if (participante == player)
					continue;
				Scoreboard playerBoard = getPlugin().getScoreboardManager().getPlayerScoreboard(participante);
				Team playerTeam = playerBoard.getTeam(tag.getTeamName());
				if (playerTeam != null)
					playerTeam.addPlayer(player);
			}
		}
	}

	public void removePlayerTag(Player player) {
		Scoreboard board = getPlugin().getScoreboardManager().getPlayerScoreboard(player);
		if (!tags.containsKey(player))
			return;
		Team t = board.getPlayerTeam(player);
		if (t != null)
			t.removePlayer(player);
		System.out.println(t.getName().substring(1, t.getName().length()));
		Tag tag = Tag.valueOf(t.getName().substring(1, 0));
		List<Player> playerList = tags.get(tag);
		playerList.remove(player);
		for (Entry<Tag, List<Player>> entry : tags.entrySet()) {
			List<Player> players = entry.getValue();
			for (Player participante : players) {
				Scoreboard playerBoard = getPlugin().getScoreboardManager().getPlayerScoreboard(participante);
				Team playerTeam = playerBoard.getTeam(tag.getTeamName());
				if (playerTeam != null)
					playerTeam.removePlayer(player);
			}
		}
	}

	@Override
	public void onDisable() {

	}
}
