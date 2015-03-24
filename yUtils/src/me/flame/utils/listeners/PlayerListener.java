package me.flame.utils.listeners;

import me.flame.utils.Main;
import me.flame.utils.permissions.enums.Group;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerListener implements Listener {
	private static boolean torneio = false;
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		event.setFormat(p.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.WHITE + "%2$s");
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
}
