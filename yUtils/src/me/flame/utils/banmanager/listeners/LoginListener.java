package me.flame.utils.banmanager.listeners;

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
			manager.unmute(mute.getMutedUuid());
			return;
		}
		if (mute.isPermanent()) {
			p.sendMessage(ChatColor.YELLOW + "Voce foi mutado permanentemente por " + mute.getMutedBy() + "! Motivo: " + ChatColor.AQUA + mute.getReason());
		} else {
			String tempo = DateUtils.formatDifference((mute.getDuration() - System.currentTimeMillis()) / 1000);
			p.sendMessage(ChatColor.YELLOW + "Voce foi mutado por " + tempo + " segundos pelo player " + mute.getMutedBy() + "! Motivo: " + ChatColor.AQUA + mute.getReason());
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		if (event.getResult() != Result.ALLOWED)
			return;
		if (!manager.isBanned(p))
			return;
		Ban ban = manager.getBan(p);
		if (ban.isUnbanned())
			return;
		if (ban.hasExpired()) {
			manager.removeTempban(ban.getBannedUuid());
			return;
		}
		event.disallow(Result.KICK_BANNED, manager.getBanMessage(ban));
	}
}
