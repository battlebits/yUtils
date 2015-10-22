package me.flame.utils.banmanager.listeners;

import java.sql.SQLException;

import me.flame.utils.banmanager.BanManagement;
import me.flame.utils.banmanager.constructors.Ban;
import me.flame.utils.banmanager.constructors.Mute;
import me.flame.utils.utils.DateUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class LoginListener implements Listener {
	private BanManagement manager;

	public LoginListener(BanManagement manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		if (!manager.isMuted(p))
			return;
		Mute mute = manager.getMute(p);
		if (mute.hasExpired()) {
			try {
				manager.unmute(mute.getMutedUuid());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return;
		}
		if (mute.isPermanent()) {
			p.sendMessage(ChatColor.YELLOW + "Voce foi mutado permanentemente por " + mute.getMutedBy() + "! Motivo: " + ChatColor.AQUA + mute.getReason());
		} else {
			String tempo = DateUtils.formatDifference((mute.getDuration() - System.currentTimeMillis()) / 1000);
			p.sendMessage(ChatColor.YELLOW + "Voce foi mutado por " + tempo + " pelo player " + mute.getMutedBy() + "! Motivo: " + ChatColor.AQUA + mute.getReason());
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsync(AsyncPlayerPreLoginEvent event) {
		try {
			manager.loadBanAndMute(event.getUniqueId());
		} catch (SQLException e) {
			event.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Nao foi possivel carregar banimento, tente novamente em breve");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent event) {
		final Player p = event.getPlayer();
		if (event.getResult() != Result.ALLOWED)
			return;
		if (!manager.isBanned(p))
			return;
		Ban ban = manager.getBan(p);
		if (ban.isUnbanned())
			return;
		if (ban.hasExpired()) {
			try {
				manager.removeTempban(ban.getBannedUuid());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return;
		}
		event.disallow(Result.KICK_BANNED, manager.getBanMessage(ban));
	}
}
