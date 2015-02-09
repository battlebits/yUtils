package me.flame.utils.payment.constructors;

import java.util.UUID;

import me.flame.utils.permissions.enums.Group;

public class Expire {
	private UUID uuid;
	private long expire;
	private Group group;

	public Expire(UUID uuid, long expire, Group group) {
		this.uuid = uuid;
		this.expire = expire;
		this.group = group;
	}

	public UUID getUuid() {
		return uuid;
	}

	public long getExpire() {
		return expire;
	}

	public Group getGroup() {
		return group;
	}

}
