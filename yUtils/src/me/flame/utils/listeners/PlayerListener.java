package me.flame.utils.listeners;

import me.flame.utils.Main;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.ranking.enums.Rank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

@SuppressWarnings("static-method")
public class PlayerListener implements Listener {
	private static boolean torneio = false;

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		Rank rank = Main.getPlugin().getRankingManager().getAccount(p).getLiga();
		event.setFormat(ChatColor.GRAY + "[" + rank.getSymbol() + ChatColor.GRAY + "] " + p.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.WHITE + "%2$s");
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		if (!torneio)
			return;
		if (Main.getPlugin().getTorneioManager().isParticipante(event.getPlayer().getUniqueId()))
			return;
		if (Main.getPlugin().getPermissionManager().hasGroupPermission(event.getPlayer(), Group.MOD))
			return;
		event.disallow(Result.KICK_WHITELIST, ChatColor.YELLOW + "Voce nao está participando do torneio");
	}

	@EventHandler
	public void onPreProcessCommand(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().toLowerCase().contains("/me")) {
			event.getPlayer().sendMessage(ChatColor.RED + "Voce nao pode utilizar o comando 'me'");
			event.setCancelled(true);
		}
		if (event.getMessage().split(" ")[0].contains(":")) {
			event.getPlayer().sendMessage(ChatColor.RED + "Voce nao pode enviar comando que possuem ':' (dois pontos)");
			event.setCancelled(true);
		}
	}
}
