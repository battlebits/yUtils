package com.renatojunior.dev.iw3.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.renatojunior.dev.iw3.classes.Application;
import com.renatojunior.dev.iw3.executors.ChargebackExecutor;
import com.renatojunior.dev.iw3.executors.DaysExecutor;
import com.renatojunior.dev.iw3.executors.OrderExecutor;

public class EventsController implements Listener {
	private final Application app;

	public EventsController(Application app) {
		this.app = app;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPrePlayerLogin(AsyncPlayerPreLoginEvent e) throws SQLException {
		UUID uuid = e.getUniqueId();

		OrderExecutor orderexecutor = new OrderExecutor(this.app, uuid, e.getName());
		ChargebackExecutor chargebackexecutor = new ChargebackExecutor(this.app, uuid, orderexecutor);
		DaysExecutor daysexecutor = new DaysExecutor(this.app, uuid, orderexecutor);

		chargebackexecutor.run();
		orderexecutor.run();
		daysexecutor.run();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent e) {
		List<String[]> chat = this.app.getChatScheduler();
		List<String[]> newchat = new ArrayList<>();
		for (String[] message : chat) {
			if (message[0] == null ? e.getPlayer().getName() == null : message[0].equals(e.getPlayer().getName())) {
				e.getPlayer().sendMessage(this.app.getChat().getMessageColor(message[1]));
			} else {
				newchat.add(message);
			}
		}
		this.app.setChatScheduler(newchat);
	}
}
