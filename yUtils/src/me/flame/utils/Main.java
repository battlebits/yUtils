package me.flame.utils;

import java.sql.Connection;

import me.flame.utils.mysql.Connect;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.ServerType;
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

	private static Main instance;

	@Override
	public void onEnable() {
		instance = this;
		connect = new Connect(this);
		mainConnection = connect.trySQLConnection("", "", "", "", "");
		ServerType type = null;
		switch (getConfig().getString("serverType")) {
		case "hungergames":
			type = ServerType.HUNGERGAMES;
			break;
		case "pvp":
			type = ServerType.PVP;
			break;
		case "lobby":
			type = ServerType.LOBBY;
			break;
		case "skywars":
			type = ServerType.SKYWARS;
			break;
		default:
			type = ServerType.NONE;
		}
		permissionManager = new PermissionManager(this, type);
		permissionManager.onEnable();
		scoreboardManager = new ScoreboardManager(this);
		scoreboardManager.onEnable();
		tagManager = new TagManager(this);
		tagManager.onEnable();
	}

	@Override
	public void onDisable() {
		Connect.SQLdisconnect(mainConnection);
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public static Main getPlugin() {
		return instance;
	}

}
