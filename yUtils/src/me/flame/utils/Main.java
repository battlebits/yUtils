package me.flame.utils;

import java.sql.Connection;

import me.flame.utils.banmanager.BanManagement;
import me.flame.utils.listeners.PlayerListener;
import me.flame.utils.mysql.Connect;
import me.flame.utils.payment.BuyManager;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.ServerType;
import me.flame.utils.scoreboard.ScoreboardManager;
import me.flame.utils.tagmanager.TagManager;
import me.flame.utils.torneio.TorneioManager;
import me.flame.utils.utils.PluginUpdater;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	/**
	 * 
	 * MySQL
	 * 
	 */
	public boolean sql = true;
	public Connect connect;
	public Connection mainConnection;

	/**
	 * 
	 * Managers
	 * 
	 */
	private PermissionManager permissionManager;
	private ScoreboardManager scoreboardManager;
	private TorneioManager torneioManager;
	private BanManagement banManager;
	private BuyManager buyManager;
	private TagManager tagManager;

	private static Main instance;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		instance = this;
		connect = new Connect(this);
		mainConnection = connect.trySQLConnection("localhost", "3306", "utils", "root", "saobestanime");
		if (!sql) {
			getServer().shutdown();
			return;
		}
		ServerType type = null;
		switch (getConfig().getString("serverType")) {
		case "hungergames":
			type = ServerType.HUNGERGAMES;
			break;
		case "battlecraft":
			type = ServerType.BATTLECRAFT;
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
		banManager = new BanManagement(this);
		banManager.onEnable();
		scoreboardManager = new ScoreboardManager(this);
		scoreboardManager.onEnable();
		tagManager = new TagManager(this);
		tagManager.onEnable();
		buyManager = new BuyManager(this);
		buyManager.onEnable();
		torneioManager = new TorneioManager(this);
		torneioManager.onEnable();
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getScheduler().runTaskTimerAsynchronously(this, new PluginUpdater(this), 0L, 108000L);
	}

	@Override
	public void onDisable() {
		permissionManager.onDisable();
		banManager.onDisable();
		scoreboardManager.onDisable();
		tagManager.onDisable();
		buyManager.onDisable();
		torneioManager.onDisable();
		Connect.SQLdisconnect(mainConnection);
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	public TorneioManager getTorneioManager() {
		return torneioManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public static Main getPlugin() {
		return instance;
	}

}
