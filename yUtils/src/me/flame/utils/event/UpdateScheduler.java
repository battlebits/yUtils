package me.flame.utils.event;

import me.flame.utils.event.UpdateEvent.UpdateType;

import org.bukkit.Bukkit;

public class UpdateScheduler implements Runnable {

	private long lastSecond = Long.MIN_VALUE;
	private long lastMinute = Long.MIN_VALUE;

	@Override
	public void run() {
		Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.TICK));
		if (lastSecond + 1000 <= System.currentTimeMillis()) {
			Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.SECOND));
			lastSecond = System.currentTimeMillis();
		}
		if (lastMinute + 60000 <= System.currentTimeMillis()) {
			Bukkit.getPluginManager().callEvent(new UpdateEvent(UpdateType.MINUTE));
			lastMinute = System.currentTimeMillis();
		}
	}

}
