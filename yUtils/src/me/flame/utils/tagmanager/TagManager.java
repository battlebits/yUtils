package me.flame.utils.tagmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.flame.utils.Main;
import me.flame.utils.Management;
import me.flame.utils.permissions.PermissionManager;
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
		for (Tag tag : Tag.values()) {
			tags.put(tag, new ArrayList<Player>());
		}
		getServer().getPluginManager().registerEvents(new JoinListener(this), getPlugin());
		getServer().getPluginManager().registerEvents(new QuitListener(this), getPlugin());
	}

	@SuppressWarnings("static-method")
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

	public static boolean isNadhyneOuGustavo(UUID uuid) {
		if (uuid.toString().equals("2a759cc7-0b01-4b7c-8f4a-a081a74dfab7"))
			return true;
		if (uuid.toString().equals("e24695ad-6618-471e-826a-2438f043a293"))
			return true;
		return false;
	}

	public static Tag getPlayerDefaultTag(Player p) {
		PermissionManager man = Main.getPlugin().getPermissionManager();
		if (TagManager.isNadhyneOuGustavo(p.getUniqueId())) {
			return Tag.ESTRELA;
		}
		if (Main.getPlugin().getTorneioManager().isParticipante(p.getUniqueId()))
			return Tag.TORNEIO;
		return Tag.valueOf(man.getPlayerGroup(p).toString());
	}

	@Override
	public void onDisable() {
		for (Player player : getServer().getOnlinePlayers()) {
			removePlayerTag(player);
		}
	}
}
