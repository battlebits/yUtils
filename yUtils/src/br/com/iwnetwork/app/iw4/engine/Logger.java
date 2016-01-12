package br.com.iwnetwork.app.iw4.engine;

import static br.com.iwnetwork.app.iw4.IW4.getPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

/**
 *
 * @author Renato
 */
public class Logger {

    private final ConsoleCommandSender console = getPlugin().getServer().getConsoleSender();

    public void log(Exception exception) {
        this.log(exception.getMessage());
    }

    public void logException(String message) {
        this.console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[IW4] &f" + message));
    }

    public void log(String message) {
        this.console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[IW4] &f" + message));
    }

}
