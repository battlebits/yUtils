package me.flame.utils.events;

import java.util.UUID;

import me.flame.utils.permissions.enums.Group;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AccountLoadEvent extends Event {
	public static final HandlerList handlers = new HandlerList();
	private UUID uuid;
	private Group group;


	public UUID getUUID() {
		return uuid;
	}

	public Group getGroup() {
		return group;
	}

	public AccountLoadEvent(UUID uuid, Group group) {
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
