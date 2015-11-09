package com.renatojunior.dev.iw3.classes;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import me.flame.utils.Main;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.renatojunior.dev.iw3.controller.CommandController;
import com.renatojunior.dev.iw3.controller.EventsController;

public class Application {
	private static Main plugin;
	private static ChatMessages chat;
	private static MysqlAPI mysql;
	private static Connection connection;
	private static List<String[]> chatschedule;

	public Application(Main instance) {
		plugin = instance;
		plugin.saveDefaultConfig();
	}

	public void run() {
		boolean loaded = false;
		int count = 0;
		try {
			chat = new ChatMessages();

			getChat().consoleMessage("&b[IW3] Loading module: Mysql ..");
			mysql = new MysqlAPI(this);
			connection = mysql.connect();
			count++;
			getChat().consoleMessage("&b[IW3] Loading module: ChatScheduler ..");
			chatschedule = new ArrayList<>();
			count++;
			getChat().consoleMessage("&b[IW3] Loading module: CommandsController ..");
			registerCommands();
			count++;
			getChat().consoleMessage("&b[IW3] Loading module: EventsController ..");
			registerEvents(plugin, new Listener[] { new EventsController(this) });
			count++;

			loaded = true;
		} catch (Exception err) {
			getChat().consoleMessage("&4[IW3] Error on Module Load");
			if (count > 0) {
				getChat().consoleMessage(err.getCause().toString());
			}
		} finally {
			getChat().consoleMessage("&f[IW3] -------------------");
			if (loaded) {
				getChat().consoleMessage("&f[IW3] Loaded modules: &a" + count + "/4");
			} else {
				getChat().consoleMessage("&f[IW3] Loaded modules: &c" + count + "/4");
			}
			getChat().consoleMessage("&f[IW3] -------------------");
		}
	}

	public void stop() {
		boolean loaded = false;
		int count = 0;
		try {
			getChat().consoleMessage("&e[IW3] Stopping module: Mysql ..");
			connection.close();
			count++;
			getChat().consoleMessage("&e[IW3] Stopping module: Application ..");
			HandlerList.unregisterAll();
			count++;

			loaded = true;
		} catch (Exception err) {
			getChat().consoleMessage("&4[IW3] Error on Module Stop");
			getChat().consoleMessage(err.getCause().toString());
		} finally {
			getChat().consoleMessage("&f[IW3] --------------------");
			if (loaded) {
				getChat().consoleMessage("&f[IW3] Stopped modules: &a" + count + "/2");
			} else {
				getChat().consoleMessage("&f[IW3] Stopped modules: &c" + count + "/2");
			}
			getChat().consoleMessage("&f[IW3] --------------------");
		}
	}

	public void registerEvents(Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}

	private CommandController registerCommands() throws Exception {
		return new CommandController(this);
	}

	public Main getPlugin() {
		return plugin;
	}

	public ChatMessages getChat() {
		return chat;
	}

	public List<String[]> getChatScheduler() {
		return chatschedule;
	}

	public void addChatScheduler(String[] message) {
		chatschedule.add(message);
	}

	public void setChatScheduler(List<String[]> newchat) {
		chatschedule = newchat;
	}

	public MysqlAPI getMysql() {
		return mysql;
	}

	public Connection getConnection() {
		return connection;
	}
}
