package me.flame.utils.banmanager.constructors;

import java.sql.Date;
import java.util.UUID;

public class Ban {
	private UUID bannedUuid;
	private String playerName;
	private String reason;
	private Date banTime;
	private Date duration;

	public Ban(UUID bannedUuid, String reason, Date banTime, Date duration) {
		this.bannedUuid = bannedUuid;
		this.reason = reason;
		this.banTime = banTime;
		this.duration = duration;
	}

	public UUID getBannedUuid() {
		return bannedUuid;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getReason() {
		return reason;
	}

	public Date getBanTime() {
		return banTime;
	}

	public Date getDuration() {
		return duration;
	}

}
