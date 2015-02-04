package me.flame.utils.banmanager.constructors;

import java.sql.Date;
import java.util.Calendar;
import java.util.UUID;

public class Ban {
	private UUID bannedUuid;
	private String reason;
	private String bannedBy;
	private Date banTime;
	private Date duration;

	public Ban(UUID bannedUuid, String bannedBy, String reason, Date banTime, Date duration) {
		this.bannedUuid = bannedUuid;
		this.bannedBy = bannedBy;
		this.reason = reason;
		this.banTime = banTime;
		this.duration = duration;
	}

	public UUID getBannedUuid() {
		return bannedUuid;
	}

	public String getBannedBy() {
		return bannedBy;
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

	public boolean hasExpired() {
		return getDuration().getTime() != 0 && getDuration().after(Calendar.getInstance().getTime());
	}

}
