package me.flame.utils.banmanager.constructors;

import java.sql.Date;
import java.util.Calendar;
import java.util.UUID;

public class Mute {
	private UUID mutedUuid;
	private String bannedBy;
	private String reason;
	private Date muteTime;
	private Date duration;

	public Mute(UUID mutedUuid, String bannedBy, String reason, Date muteTime, Date duration) {
		this.mutedUuid = mutedUuid;
		this.bannedBy = bannedBy;
		this.reason = reason;
		this.muteTime = muteTime;
		this.duration = duration;
	}

	public UUID getMutedUuid() {
		return mutedUuid;
	}

	public String getBannedBy() {
		return bannedBy;
	}

	public String getReason() {
		return reason;
	}

	public Date getMuteTime() {
		return muteTime;
	}

	public Date getDuration() {
		return duration;
	}

	public boolean hasExpired() {
		return getDuration().getTime() != 0 && getDuration().after(Calendar.getInstance().getTime());
	}
}
