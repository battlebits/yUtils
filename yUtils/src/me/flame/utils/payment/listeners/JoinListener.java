package me.flame.utils.payment.listeners;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.scheduler.BukkitRunnable;

import me.flame.utils.payment.BuyManager;
import me.flame.utils.payment.constructors.Expire;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.tagmanager.TagManager;

public class JoinListener implements Listener {
	private BuyManager manager;

	public JoinListener(BuyManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsync(AsyncPlayerPreLoginEvent event) {
		try {
			manager.loadExpire(event.getUniqueId());
		} catch (Exception e) {
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "Nao foi possivel reconhecer seus status de compras, tente novamente");
		}
		final UUID uuid = event.getUniqueId();
		if (!manager.expires.containsKey(uuid))
			return;
		final Expire expire = manager.getExpire(uuid);
		if (expire == null)
			return;
		if (expire.getExpire() < System.currentTimeMillis()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (manager.getPlugin().getPermissionManager().getPlayerGroup(expire.getUuid()) == expire.getGroup())
						manager.getPlugin().getPermissionManager().setPlayerGroup(expire.getUuid(), Group.NORMAL);
					try {
						manager.getPlugin().getPermissionManager().removeRank(expire.getUuid());
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					try {
						manager.removeExpire(expire.getUuid());
					} catch (Exception e) {
						e.printStackTrace();
					}
					manager.expires.remove(uuid);

					new BukkitRunnable() {
						@Override
						public void run() {
							Player target = Bukkit.getPlayer(uuid);
							if (target != null) {
								target.sendMessage(ChatColor.RED + "---------------------------BATTLEBITS------------------------------");
								target.sendMessage("");
								target.sendMessage(ChatColor.RED + "Seu vip expirou! Para comprar novamente entre no site http://loja.battlebits.com.br");
								target.sendMessage("");
								target.sendMessage(ChatColor.RED + "-------------------------------------------------------------------");
								manager.getPlugin().getTagManager().addPlayerTag(target, TagManager.getPlayerDefaultTag(target));
							}
						}
					}.runTaskLater(manager.getPlugin(), 5);
				}
			}.runTaskLaterAsynchronously(manager.getPlugin(), 40);
		}
	}
}
