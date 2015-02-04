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
			tags.put(tag, new ArrayList<>());
		}
		getServer().getPluginManager().registerEvents(new JoinListener(this), getPlugin());
		getServer().getPluginManager().registerEvents(new QuitListener(this), getPlugin());
	}

	public void addPlayerTag(Player player, Tag tag) {
		Scoreboard board = getPlugin().getScoreboardManager().getPlayerScoreboard(player);
		boolean update = false;
		for (Tag teamTag : Tag.values()) {
			Team team = board.getTeam(teamTag.getTeamName());
			if (team != null) {
				update = true;
				break;
			}
			team = board.registerNewTeam(teamTag.getTeamName());
			team.setPrefix(teamTag.getPrefix());
			team.setSuffix(ChatColor.RESET + "");
		}
		if (update) {
			updateTag(player, tag);
			return;
		}
		player.setDisplayName(tag.getPrefix() + player.getName() + ChatColor.RESET);
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
		Tag tag = Tag.valueOf(getPlugin().getPermissionManager().getPlayerGroup(player).toString());
		for (Entry<Tag, List<Player>> entry : tags.entrySet()) {
			List<Player> players = entry.getValue();
			for (Player participante : players) {
				Scoreboard playerBoard = getPlugin().getScoreboardManager().getPlayerScoreboard(participante);
				Team playerTeam = playerBoard.getTeam(tag.getTeamName());
				if (playerTeam != null) {
					playerTeam.removePlayer(player);
				}
				participante.setScoreboard(playerBoard);
			}
		}
		List<Player> playerList = tags.get(tag);
		playerList.remove(player);
		tags.put(tag, playerList);
	}

	public void updateTag(Player p, Tag tagToGo) {
		Tag tag = Tag.valueOf(getPlugin().getPermissionManager().getPlayerGroup(p).toString());
		List<Player> playerList = tags.get(tag);
		playerList.remove(p);
		tags.put(tag, playerList);
		for (Entry<Tag, List<Player>> entry : tags.entrySet()) {
			List<Player> players = entry.getValue();
			for (Player participante : players) {
				Scoreboard playerBoard = getPlugin().getScoreboardManager().getPlayerScoreboard(participante);
				if (playerBoard.getPlayerTeam(p) != null)
					playerBoard.getPlayerTeam(p).removePlayer(p);
				Team team = playerBoard.getTeam(tagToGo.getTeamName());
				if (team != null)
					team.addPlayer(p);
				participante.setScoreboard(playerBoard);
			}
		}
		p.setDisplayName(tag.getPrefix() + p.getName() + ChatColor.RESET);
		List<Player> playerList2 = tags.get(tagToGo);
		playerList2.add(p);
		tags.put(tagToGo, playerList2);
	}

	@Override
	public void onDisable() {
		for (Player player : getServer().getOnlinePlayers()) {
			removePlayerTag(player);
		}
	}
}
