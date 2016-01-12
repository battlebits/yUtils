package me.flame.utils.ranking.listeners;

import java.util.UUID;

import me.flame.utils.ranking.RankingManager;
import me.flame.utils.ranking.constructors.Account;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

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
		UUID uuid = event.getPlayer().getUniqueId();
		manager.removeAccount(uuid);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKick(PlayerKickEvent event) {
		final Account account = manager.getAccount(event.getPlayer());
		if (account.getLock().isLocked()) {
			event.setCancelled(true);
			final Player p = event.getPlayer();
			final String str = event.getReason();
			new BukkitRunnable() {
				@Override
				public void run() {
					account.getLock().lock();
					account.getLock().unlock();
					new BukkitRunnable() {
						@Override
						public void run() {
							p.kickPlayer(str);
						}
					}.runTask(manager.getPlugin());
				}
			}.runTaskAsynchronously(manager.getPlugin());
		}
	}

}
