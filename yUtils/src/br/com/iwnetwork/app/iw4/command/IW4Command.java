package br.com.iwnetwork.app.iw4.command;

import static br.com.iwnetwork.app.iw4.IW4.initConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Renato
 */
public class IW4Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("iw4.command.iw4") && !sender.hasPermission("iw4.*")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
        }
        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
                initConfig();
                sender.sendMessage(ChatColor.GREEN + "Reloaded.");
            } else if (args.length == 1 && "help".equalsIgnoreCase(args[0])) {
                sender.sendMessage(ChatColor.GREEN + "-------------------------------------------");
                sender.sendMessage(ChatColor.GREEN + "- IW4 HELP MENU");
                sender.sendMessage(ChatColor.GREEN + "-------------------------------------------");
                sender.sendMessage(ChatColor.GRAY + "- iw4 reload " + ChatColor.WHITE + "- reloads the plugin");
                sender.sendMessage(ChatColor.GREEN + "-------------------------------------------");
            } else {
                sender.sendMessage(ChatColor.RED + "Try \"iw4 help\"");
            }
        } else if (sender instanceof Player) {
            if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
                initConfig();
                sender.sendMessage(ChatColor.GREEN + "Reloaded.");
            } else if (args.length == 1 && "help".equalsIgnoreCase(args[0])) {
                sender.sendMessage(ChatColor.GREEN + "-------------------------------------------");
                sender.sendMessage(ChatColor.GREEN + "- IW4 HELP MENU");
                sender.sendMessage(ChatColor.GREEN + "-------------------------------------------");
                sender.sendMessage(ChatColor.GRAY + "- /iw4 reload " + ChatColor.WHITE + "- reloads the plugin");
                sender.sendMessage(ChatColor.GREEN + "-------------------------------------------");
            } else {
                sender.sendMessage(ChatColor.RED + "Try \"/iw4 help\"");
            }
        }
        return false;
    }

}
