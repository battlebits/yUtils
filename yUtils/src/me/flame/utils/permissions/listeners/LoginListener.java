package me.flame.utils.permissions.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.flame.utils.Main;
import me.flame.utils.permissions.enums.Group;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitRunnable;

public class LoginListener implements Listener {
	private Main main;
	private final Map<UUID, PermissionAttachment> attachments;

	public LoginListener(Main main) {
		attachments = new HashMap<>();
		this.main = main;
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : main.getServer().getOnlinePlayers()) {
					updateAttachment(player, Group.DONO);
				}
			}
		}.runTaskLater(main, 5);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		updateAttachment(player, Group.DONO);
	}

	protected void updateAttachment(Player player, Group group) {
		PermissionAttachment attach = (PermissionAttachment) attachments.get(player.getUniqueId());
		Permission playerPerm = getCreateWrapper(player, player.getUniqueId().toString());
		if (attach == null) {
			attach = player.addAttachment(main);
			attachments.put(player.getUniqueId(), attach);
			attach.setPermission(playerPerm, true);
		}
		playerPerm.getChildren().clear();
		for (String perm : group.getGroup().getPermissions()) {
			if (!playerPerm.getChildren().containsKey(perm)) {
				playerPerm.getChildren().put(perm, true);
			}
		}
		player.recalculatePermissions();
	}

	private Permission getCreateWrapper(Player player, String name) {
		Permission perm = this.main.getServer().getPluginManager().getPermission(name);
		if (perm == null) {
			perm = new Permission(name, "Permissao interna", PermissionDefault.FALSE);
			this.main.getServer().getPluginManager().addPermission(perm);
		}
		return perm;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		removeAttachment(event.getPlayer());
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		removeAttachment(event.getPlayer());
	}

	protected void removeAttachment(Player player) {
		PermissionAttachment attach = (PermissionAttachment) this.attachments.remove(player.getUniqueId());
		if (attach != null) {
			attach.remove();
		}
		this.main.getServer().getPluginManager().removePermission(player.getUniqueId().toString());
	}

	public void onDisable() {
		for (PermissionAttachment attach : attachments.values()) {
			attach.remove();
		}
		attachments.clear();
	}
}
