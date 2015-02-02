package me.flame.utils;

import java.sql.Connection;

import me.flame.utils.mysql.Connect;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.scoreboard.ScoreboardManager;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	/**
	 * 
	 * MySQL
	 * 
	 */
	public boolean sql = false;
	public Connect connect;
	public Connection mainConnection;

	/**
	 * 
	 * Managers
	 * 
	 */
	public PermissionManager permissionManager;
	private ScoreboardManager scoreboardManager;

	@Override
	public void onEnable() {
		connect = new Connect(this);
		mainConnection = connect.trySQLConnection("", "", "", "", "");

		permissionManager = new PermissionManager(this);
		permissionManager.onEnable();
		scoreboardManager = new ScoreboardManager(this);
		scoreboardManager.onEnable();
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

}
