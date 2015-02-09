package me.flame.utils.payment.listeners;

import me.flame.utils.events.AccountLoadEvent;
import me.flame.utils.payment.BuyManager;
import me.flame.utils.payment.constructors.Expire;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.Group;
import me.flame.utils.tagmanager.enums.Tag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {
	private BuyManager manager;

	public JoinListener(BuyManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(AccountLoadEvent event) {
		if (!manager.expires.containsKey(event.getUUID()))
			return;
		Expire expire = manager.getExpire(event.getUUID());
		if (expire == null)
			return;
		if (expire.getExpire() < System.currentTimeMillis()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					manager.getPlugin().getPermissionManager().setPlayerGroup2(expire.getUuid(), Group.NORMAL);
					manager.getPlugin().getPermissionManager().removePlayer(expire.getUuid());
					manager.removeExpire(expire.getUuid());
					manager.expires.remove(event.getUUID());
					Player target = Bukkit.getPlayer(event.getUUID());
					if (target != null) {
						target.sendMessage(ChatColor.RED + "---------------------------BATTLEBITS------------------------------");
						target.sendMessage("");
						target.sendMessage(ChatColor.RED + "Seu vip expirou! Para comprar novamente entre no site http://loja.battlecraft.com.br");
						target.sendMessage("");
						target.sendMessage(ChatColor.RED + "-------------------------------------------------------------------");
						new BukkitRunnable() {
							@Override
							public void run() {
								manager.getPlugin().getTagManager().addPlayerTag(target, getPlayerDefaultTag(target));
							}
						}.runTask(manager.getPlugin());
					}
				}
			}.runTaskLaterAsynchronously(manager.getPlugin(), 40);
		}
	}

	private Tag getPlayerDefaultTag(Player p) {
		PermissionManager man = manager.getPlugin().getPermissionManager();
		return Tag.valueOf(man.getPlayerGroup(p).toString());
	}
}
