package me.flame.utils.banmanager.listeners;

import me.flame.utils.banmanager.BanManagement;
import me.flame.utils.banmanager.constructors.Ban;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class LoginListener implements Listener {
	private BanManagement manager;

	public LoginListener(BanManagement manager) {
		this.manager = manager;
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		if (!manager.isBanned(p))
			return;
		Ban ban = manager.getBan(p);
		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.YELLOW + "Voce foi banido do servidor!");
		builder.append("\n" + ban.getBannedBy() + " baniu voce! Motivo: " + ban.getReason());
		builder.append("\n");
		builder.append("\nPara mais informacoes: http://battlebits.com/banimento?uuid=" + ban.getBannedUuid().toString().replace("-", ""));
		event.disallow(Result.KICK_BANNED, builder.toString());
	}
}
