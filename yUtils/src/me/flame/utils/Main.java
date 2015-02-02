package me.flame.utils;

import java.sql.Connection;

import me.flame.utils.mysql.Connect;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.scoreboard.ScoreboardManager;
import me.flame.utils.tagmanager.TagManager;

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
	private PermissionManager permissionManager;
	private ScoreboardManager scoreboardManager;
	private TagManager tagManager;

	@Override
	public void onEnable() {
		connect = new Connect(this);
		mainConnection = connect.trySQLConnection("", "", "", "", "");

		permissionManager = new PermissionManager(this);
		permissionManager.onEnable();
		scoreboardManager = new ScoreboardManager(this);
		scoreboardManager.onEnable();
		tagManager = new TagManager(this);
		tagManager.onEnable();
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

}
