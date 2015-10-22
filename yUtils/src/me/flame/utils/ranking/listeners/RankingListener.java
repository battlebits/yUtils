package me.flame.utils.ranking.listeners;

import me.flame.utils.ranking.RankingManager;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class RankingListener implements Listener {

	private RankingManager manager;

	public RankingListener(RankingManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(AsyncPlayerPreLoginEvent event) {
		try {
			manager.loadAccount(event.getUniqueId());
		} catch (Exception e) {
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "Nao foi possivel carregar sua conta, tente novamente mais tarde");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		try {
			manager.removeAccount(event.getPlayer().getUniqueId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
