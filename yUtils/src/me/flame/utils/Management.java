package me.flame.utils;

import java.sql.Connection;

import org.bukkit.Server;

public abstract class Management {
	private Main main;

	public Management(Main main) {
		this.main = main;
	}

	public abstract void onEnable();

	public Server getServer() {
		return main.getServer();
	}

	public Main getPlugin() {
		return main;
	}

	public Connection getMySQL() {
		return main.mainConnection;
	}
}
