package me.flame.utils.banmanager.listeners;

import me.flame.utils.banmanager.BanManagement;
import me.flame.utils.banmanager.constructors.Ban;
import me.flame.utils.banmanager.constructors.Mute;
import me.flame.utils.banmanager.utils.DateUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class LoginListener implements Listener {
	private BanManagement manager;

	public LoginListener(BanManagement manager) {
		this.manager = manager;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		if (!manager.isMuted(p))
			return;
		Mute mute = manager.getMute(p);
		if (mute.hasExpired()) {
			manager.unmute(mute.getMutedUuid());
			return;
		}
		// TODO Rever sistema de tempban
		if (mute.isPermanent()) {
			p.sendMessage(ChatColor.YELLOW + "Voce foi mutado permanentemente por " + mute.getMutedBy() + "! Motivo: " + ChatColor.AQUA + mute.getReason());
		} else {
			String tempo = DateUtils.formatDifference((mute.getDuration() - System.currentTimeMillis()) / 1000);
			p.sendMessage(ChatColor.YELLOW + "Voce foi mutado por " + tempo + " segundos pelo player " + mute.getMutedBy() + "! Motivo: " + ChatColor.AQUA + mute.getReason());
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		if (!manager.isBanned(p))
			return;
		Ban ban = manager.getBan(p);
		if (ban.isUnbanned())
			return;
		if (ban.hasExpired()) {
			manager.removeTempban(ban.getBannedUuid());
		}
		StringBuilder builder = new StringBuilder();
		if (ban.isPermanent()) {
			builder.append(ChatColor.YELLOW + "Voce foi banido do servidor!");
			builder.append("\n" + ban.getBannedBy() + " baniu voce! Motivo: " + ban.getReason());
		} else {
			String tempo = DateUtils.formatDifference((ban.getDuration() - System.currentTimeMillis()) / 1000);
			builder.append(ChatColor.YELLOW + "Voce foi temporariamente banido do servidor!");
			builder.append("\nBanimento durara " + tempo);
			builder.append("\n" + ban.getBannedBy() + " baniu voce! Motivo: " + ban.getReason());
		}
		event.disallow(Result.KICK_BANNED, builder.toString());
	}
}
