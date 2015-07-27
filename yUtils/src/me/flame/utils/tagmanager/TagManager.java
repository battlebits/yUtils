package me.flame.utils.tagmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.tagmanager.enums.Tag;
import me.flame.utils.tagmanager.listeners.JoinListener;
import me.flame.utils.tagmanager.listeners.QuitListener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TagManager extends Management {
	private HashMap<Tag, List<Player>> tags;

	public TagManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		tags = new HashMap<>();
		for (Tag tag : Tag.values()) {
			tags.put(tag, new ArrayList<Player>());
		}
		getServer().getPluginManager().registerEvents(new JoinListener(this), getPlugin());
		getServer().getPluginManager().registerEvents(new QuitListener(this), getPlugin());
	}

	private String getName(String kit) {
		char[] stringArray = kit.toLowerCase().toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		return new String(stringArray);
	}

	public void addPlayerTag(Player player, Tag tag) {
		removePlayerTag(player);
		player.setDisplayName(tag.getPrefix() + player.getName() + ChatColor.RESET);
		Scoreboard board = getPlugin().getScoreboardManager().getPlayerScoreboard(player);
		for (Tag teamTag : Tag.values()) {
			Team team = board.getTeam(teamTag.getTeamName());
			if (team != null) {
				continue;
			}
			team = board.registerNewTeam(teamTag.getTeamName());
			team.setPrefix(teamTag.getPrefix());
			team.setSuffix(ChatColor.RESET + "");
			team.setCanSeeFriendlyInvisibles(false);
			team.setDisplayName(getName(teamTag.getTeamName().substring(1, team.getName().length())));
		}
		List<Player> playerList = tags.get(tag);
		playerList.add(player);
		tags.put(tag, playerList);
		for (Entry<Tag, List<Player>> entry : tags.entrySet()) {
			Tag tagteam = entry.getKey();
			List<Player> players = entry.getValue();
			Team team = board.getTeam(tagteam.getTeamName());
			for (Player participante : players) {
				if (team != null)
					team.addPlayer(participante);
				Scoreboard playerBoard = getPlugin().getScoreboardManager().getPlayerScoreboard(participante);
				Team playerTeam = playerBoard.getTeam(tag.getTeamName());
				if (playerTeam != null)
					playerTeam.addPlayer(player);
			}
		}
	}

	public void removePlayerTag(Player player) {
		for (Player participante : getServer().getOnlinePlayers()) {
			Scoreboard playerBoard = getPlugin().getScoreboardManager().getPlayerScoreboard(participante);
			Team playerTeam = playerBoard.getPlayerTeam(player);
			if (playerTeam != null)
				playerTeam.removePlayer(player);
			participante.setScoreboard(playerBoard);
		}
		for (Entry<Tag, List<Player>> entry : tags.entrySet()) {
			List<Player> players = entry.getValue();
			players.remove(player);
			tags.put(entry.getKey(), players);
		}
	}

	@Override
	public void onDisable() {
		for (Player player : getServer().getOnlinePlayers()) {
			removePlayerTag(player);
		}
	}
}
