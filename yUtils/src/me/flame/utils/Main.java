package me.flame.utils;

import java.sql.Connection;

import me.flame.utils.banmanager.BanManagement;
import me.flame.utils.commands.Account;
import me.flame.utils.commands.Fake;
import me.flame.utils.commands.Givekit;
import me.flame.utils.commands.Rank;
import me.flame.utils.commands.TagCommand;
import me.flame.utils.injector.Injector;
import me.flame.utils.listeners.PlayerListener;
import me.flame.utils.mysql.Connect;
import me.flame.utils.nms.Utils;
import me.flame.utils.payment.BuyManager;
import me.flame.utils.permissions.PermissionManager;
import me.flame.utils.permissions.enums.ServerType;
import me.flame.utils.ranking.RankingManager;
import me.flame.utils.scoreboard.ScoreboardManager;
import me.flame.utils.tagmanager.TagManager;
import me.flame.utils.torneio.TorneioManager;
import me.flame.utils.utils.PluginUpdater;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.renatojunior.dev.iw3.classes.Application;
import com.renatojunior.dev.iw3.controller.CommandController;

public class Main extends JavaPlugin {

	/**
	 * 
	 * MySQL
	 * 
	 */
	public boolean sql = true;
	public String host = "";
	public String password = "";
	public String user = "";
	public String port = "3306";
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
	private RankingManager rankingManager;
	private Application app;
	
	private static Main instance;

	@Override
	public void onEnable() {
		if (Utils.version.startsWith("v1_7"))
			Injector.createTinyProtocol(this);
		saveDefaultConfig();
		instance = this;
		prepareConfig();
		connect = new Connect(this);
		mainConnection = connect.trySQLConnection();
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
		case "raid":
			type = ServerType.RAID;
			break;
		default:
			type = ServerType.NONE;
		}
		getLogger().info("Carregando Config!");
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
		rankingManager = new RankingManager(this);
		rankingManager.onEnable();
		app = new Application(this);
		app.run();
		getPlugin().getCommand("fake").setExecutor(new Fake(type));
		getPlugin().getCommand("givekit").setExecutor(new Givekit(this));
		getPlugin().getCommand("tag").setExecutor(new TagCommand(this));
		getPlugin().getCommand("account").setExecutor(new Account(this));
		getPlugin().getCommand("rank").setExecutor(new Rank());
		getCommand("iw3").setExecutor(new CommandController(app));
		getCommand("compras").setExecutor(new CommandController(app));
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getScheduler().runTaskTimerAsynchronously(this, new PluginUpdater(this), 2L, 108000L);
	}

	@Override
	public void onDisable() {
		if (permissionManager != null)
			permissionManager.onDisable();
		if (banManager != null)
			banManager.onDisable();
		if (scoreboardManager != null)
			scoreboardManager.onDisable();
		if (tagManager != null)
			tagManager.onDisable();
		if (buyManager != null)
			buyManager.onDisable();
		if (torneioManager != null)
			torneioManager.onDisable();
		if (rankingManager != null)
			rankingManager.onDisable();
		app.stop();
		Connect.SQLdisconnect(mainConnection);
	}

	private void prepareConfig() {
		FileConfiguration c = getConfig();
		sql = c.getBoolean("sql");
		host = c.getString("sql-host");
		password = c.getString("sql-pass");
		user = c.getString("sql-user");
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public RankingManager getRankingManager() {
		return rankingManager;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	public TorneioManager getTorneioManager() {
		return torneioManager;
	}

	public BuyManager getBuyManager() {
		return buyManager;
	}

	public BanManagement getBanManager() {
		return banManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public static Main getPlugin() {
		return instance;
	}

}
