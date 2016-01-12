package br.com.iwnetwork.app.iw4;

import org.bukkit.plugin.PluginManager;

import br.com.iwnetwork.app.iw4.IW4Handler;
import br.com.iwnetwork.app.iw4.command.AbstractCommand;
import br.com.iwnetwork.app.iw4.command.IW4Command;
import br.com.iwnetwork.app.iw4.command.PackagesCommand;
import br.com.iwnetwork.app.iw4.controller.ControllerStartup;
import br.com.iwnetwork.app.iw4.engine.Registry;
import br.com.iwnetwork.app.iw4.util.Config;
import me.flame.utils.Main;

/**
 *
 * @author Renato
 */
public final class IW4 {

    private static Config config;
    private static Main plugin;
    private static Registry registry;
    private static IW4Handler handler;

    public void onEnable(Main main) {
    	
    	
        plugin = main;
        registry = new Registry();
        handler = new IW4Handler();

        // Config
        config = new Config();

        // Init
        registry.init();

        // Startup
        ControllerStartup cs = new ControllerStartup();
        cs.init();

        // Events
        getPm().registerEvents(handler, getPlugin());

        // Commands
        main.getCommand("iw4").setExecutor(new IW4Command());
        AbstractCommand packagesCommand = new PackagesCommand(getPluginConfig().getString("cmd_packages"));
        packagesCommand.register();
    }

    public void onDisable() {
        plugin = null;
        registry = null;
    }

    public static Config getPluginConfig() {
        return config;
    }

    public static void initConfig() {
        // Load
        config = new Config();
        // Config
        getPluginConfig().setProperty("debug", getPluginConfig().getString("debug_mode"));
        getPluginConfig().setProperty("api_url", getPluginConfig().getString("api_auth_url") + "/");
        getPluginConfig().setProperty("api_key", getPluginConfig().getString("api_auth_key"));
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static PluginManager getPm() {
        return plugin.getServer().getPluginManager();
    }

    public static Registry getRegistry() {
        return registry;
    }

    public static IW4Handler getHandler() {
        return handler;
    }

}
