package me.flame.utils.events;

import java.util.UUID;

import me.flame.utils.permissions.enums.Group;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AccountLoadEvent extends Event {
	public static final HandlerList handlers = new HandlerList();
	private Player player;
	private UUID uuid;
	private Group group;

	public Player getPlayer() {
		return player;
	}

	public UUID getUUID() {
		return uuid;
	}

	public Group getGroup() {
		return group;
	}

	public AccountLoadEvent(Player player, UUID uuid, Group group) {
		this.player = player;
		this.uuid = uuid;
		this.group = group;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
