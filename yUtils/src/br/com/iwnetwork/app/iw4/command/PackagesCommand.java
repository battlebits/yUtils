package br.com.iwnetwork.app.iw4.command;

import static br.com.iwnetwork.app.iw4.IW4.getPlugin;
import static br.com.iwnetwork.app.iw4.IW4.getPluginConfig;
import static br.com.iwnetwork.app.iw4.system.Functions.isValidMD5;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.iwnetwork.app.iw4.object.IW4PlayerPackage;
import br.com.iwnetwork.app.iw4.request.RequestPlayerPackages;

/**
 *
 * @author Renato
 */
public class PackagesCommand extends AbstractCommand {

	public PackagesCommand(String command) {
		super(command, "/" + command, "IW4 Packages Command");
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		final CommandSender sender = s;
		final String[] args = a;
		if (!sender.hasPermission("iw4.command.package") || !sender.hasPermission("iw4.*")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
			return false;
		}
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				String uuid = null;
				if (sender instanceof ConsoleCommandSender) {
					if (args.length != 1) {
						sender.sendMessage(ChatColor.RED + command + " {player|uuid}");
						return;
					}
					String pre_md5 = args[0];
					if (pre_md5.contains("-")) {
						pre_md5 = pre_md5.replace("-", "");
					}
					if (!isValidMD5(pre_md5)) {
						Player player = getPlugin().getServer().getPlayerExact(args[0]);
						if (player == null) {
							sender.sendMessage(ChatColor.RED + "Player not found. Try \"" + command + " {uuid}\"");
							return;
						}
						uuid = player.getUniqueId().toString();
					} else {
						uuid = pre_md5;
					}
				} else if (sender instanceof Player) {
					if (sender.hasPermission("iw4.admin") && args.length > 0) {
						if (args.length != 1) {
							sender.sendMessage(ChatColor.RED + "/" + command + " {player|uuid}");
							return;
						}
						String pre_md5 = args[0];
						if (pre_md5.contains("-")) {
							pre_md5 = pre_md5.replace("-", "");
						}
						if (!isValidMD5(pre_md5)) {
							Player player = getPlugin().getServer().getPlayerExact(args[0]);
							if (player == null) {
								sender.sendMessage(ChatColor.RED + "Player not found. Try \"/" + command + " {uuid}\"");
								return;
							}
							uuid = player.getUniqueId().toString();
						} else {
							uuid = pre_md5;
						}
					} else {
						if (args.length > 0) {
							sender.sendMessage(ChatColor.RED + "Try \"/" + command + "\"");
						}
						Player player = (Player) sender;
						uuid = player.getUniqueId().toString();
					}
				}

				if (uuid != null && uuid.contains("-")) {
					uuid = uuid.replace("-", "");
				}

				if (uuid == null || uuid.isEmpty()) {
					sender.sendMessage(ChatColor.RED + "Error! Try again.");
					return;
				}

				uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");

				HashMap<Object, Object> requestData = new HashMap<>();
				requestData.put("uuid", uuid);
				requestData.put("server", getPluginConfig().getString("api_config_server"));

				RequestPlayerPackages pp = new RequestPlayerPackages(requestData);
				HashMap<Object, IW4PlayerPackage> response = pp.request();

				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e-------------------------------------------"));
				if (response.isEmpty()) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e- " + getPluginConfig().getString("text_no_pkg")));
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e- " + getPluginConfig().getString("text_showing_pkg").replace("{0}", String.valueOf(response.size()))));
					for (Map.Entry<Object, IW4PlayerPackage> entry : response.entrySet()) {
						String product_name = entry.getValue().getProductName();
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7-------------------------------------------"));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- " + getPluginConfig().getString("text_product") + ": &a" + product_name));
						if (!entry.getValue().getLifetime()) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- " + getPluginConfig().getString("text_days_remaining") + ": " + entry.getValue().getProductDays()));
						} else {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- [Lifetime]"));
						}
					}

				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7-------------------------------------------"));

			}
		}.runTask(getPlugin());
		return false;
	}

}
