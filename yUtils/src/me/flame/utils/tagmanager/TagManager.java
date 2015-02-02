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

public class TagManager extends Management {
	private HashMap<Tag, List<Player>> tags;

	public TagManager(Main main) {
		super(main);
	}

	@Override
	public void onEnable() {
		tags = new HashMap<>();
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
				if (team != null) {
					team.addPlayer(participante);
				}
				Scoreboard playerBoard = getPlugin().getScoreboardManager().getPlayerScoreboard(player);
				Team playerTeam = playerBoard.getTeam(tag.getTeamName());
				if (playerTeam != null)
					playerTeam.addPlayer(player);
			}
		}
	}
}
