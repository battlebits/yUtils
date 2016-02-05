package br.com.battlebits.iw4;

import static br.com.battlebits.iw4.IW4.getPlugin;
import static br.com.battlebits.iw4.IW4.getPluginConfig;
import static br.com.battlebits.iw4.IW4.getPm;
import static br.com.battlebits.iw4.IW4.getRegistry;
import static br.com.battlebits.iw4.system.Functions.mapStringReplace;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import br.com.battlebits.iw4.api.event.IW4PostPendingPlayerEvent;
import br.com.battlebits.iw4.engine.Event;
import br.com.battlebits.iw4.object.IW4OrderProduct;
import br.com.battlebits.iw4.request.RequestPlayerExpiredPackages;
import br.com.battlebits.iw4.request.RequestPlayerPendingPackages;
import br.com.battlebits.iw4.request.RequestPlayerProductAdd;
import br.com.battlebits.iw4.request.RequestPlayerProductRemove;
import br.com.battlebits.iw4.request.RequestPlayerSetOrders;

/**
 *
 * @author Renato
 */
public class IW4Handler extends Event implements Listener {

	public IW4Handler() {
		this.data = new HashMap<>();
	}

	public void registerCommand(String... aliases) {
		try {
			PluginCommand command;
			command = getCommand(aliases[0], getPlugin());
			command.setAliases(Arrays.asList(aliases));
			getCommandMap().register(getPlugin().getDescription().getName(), command);
		} catch (InvocationTargetException e) {
			getRegistry().logger().log(e);
		}
	}

	private static PluginCommand getCommand(String name, Plugin plugin) throws InvocationTargetException {
		PluginCommand command = null;
		try {
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);
			command = c.newInstance(name, plugin);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			getRegistry().logger().log(e);
		}
		return command;
	}

	private static CommandMap getCommandMap() {
		CommandMap commandMap = null;
		try {
			if (getPm() instanceof SimplePluginManager) {
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);

				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			getRegistry().logger().log(e);
		}
		return commandMap;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLoginEvent(AsyncPlayerPreLoginEvent e) {

		UUID uuid = e.getUniqueId();
		String server = getPluginConfig().getString("api_config_server");

		HashMap<Object, Object> requestData = new HashMap<>();
		requestData.put("uuid", uuid.toString());
		requestData.put("player", e.getName());
		requestData.put("server", server);

		// New Packages
		RequestPlayerPendingPackages requestplayerpendingpackages = new RequestPlayerPendingPackages(requestData);
		HashMap<Object, IW4OrderProduct> request_pp = requestplayerpendingpackages.request();

		if (!request_pp.isEmpty()) {

			RequestPlayerSetOrders requestplayersetorders = new RequestPlayerSetOrders(requestData);
			HashMap<Object, IW4OrderProduct> request_pso = requestplayersetorders.request(request_pp);

			HashMap<Object, IW4OrderProduct> response = new HashMap<>();
			int i = 0;
			for (Map.Entry<Object, IW4OrderProduct> e_pso : request_pso.entrySet()) {
				for (Map.Entry<Object, IW4OrderProduct> e_ppo : request_pp.entrySet()) {
					if (Objects.equals(e_pso.getValue().getProductId(), e_ppo.getValue().getProductId())) {
						response.put(i, e_ppo.getValue());
						break;
					}
				}
				i++;
			}

			if (!response.isEmpty()) {
				for (Map.Entry<Object, IW4OrderProduct> product : response.entrySet()) {
					if (product.getValue().getEvent().getEventType().onStart()) {
						RequestPlayerProductAdd ppa = new RequestPlayerProductAdd(requestData);
						ppa.request(product.getValue());
					} else if (product.getValue().getEvent().getEventType().onStop()) {
						RequestPlayerProductRemove ppo = new RequestPlayerProductRemove(requestData);
						ppo.request(product.getValue());
					}
				}
			}

		}

		// Expired / Updated packages
		RequestPlayerExpiredPackages pep = new RequestPlayerExpiredPackages(requestData);
		pep.request();

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostPendingPlayerEvent(IW4PostPendingPlayerEvent e) {
		HashMap<Object, IW4OrderProduct> response = e.getPendingPackages();
		for (Map.Entry<Object, IW4OrderProduct> product : response.entrySet()) {
			if (product.getValue().getEvent().getEventType().onStart()) {
				getRegistry().logger().log("&e-------------------------------------------");
				getRegistry().logger().log("&e- " + e.getPlayer().getPlayerName() + " - Pending package");

				String product_name = product.getValue().getProductName();
				String product_quantity = String.valueOf(product.getValue().getProductQuantity());
				getRegistry().logger().log("&7-------------------------------------------");
				getRegistry().logger().log("&7- " + getPluginConfig().getString("text_product") + ": &a" + product_name + " (x" + product_quantity + ")");
				getRegistry().logger().log("&7- " + getPluginConfig().getString("text_commands") + ":");
				HashMap<String, Object> hashmap = new HashMap<>();
				hashmap.put("player", e.getPlayer().getPlayerName());
				hashmap.put("uuid", e.getPlayer().getUUID().toString());
				hashmap.put("product", product.getValue().getProductName());
				hashmap.put("quantity", String.valueOf(product.getValue().getProductQuantity()));
				hashmap.put("duration_single", String.valueOf(product.getValue().getDurationSingle()));
				hashmap.put("duration", String.valueOf(product.getValue().getDuration()));
				hashmap.put("lifetime", String.valueOf(product.getValue().getLifetime()));
				for (int i = 0; i < product.getValue().getCommands().size(); i++) {
					getRegistry().logger().log("&7- &b" + mapStringReplace(product.getValue().getCommands().get(i), hashmap));
				}
				getRegistry().logger().log("&7-------------------------------------------");
			} else if (product.getValue().getEvent().getEventType().onStop()) {
				getRegistry().logger().log("&c-------------------------------------------");
				getRegistry().logger().log("&c- " + e.getPlayer().getPlayerName() + " - Expired package");
				String product_name = product.getValue().getProductName();
				getRegistry().logger().log("&7-------------------------------------------");
				getRegistry().logger().log("&7- " + getPluginConfig().getString("text_product") + ": &f" + product_name);
				getRegistry().logger().log("&7- " + getPluginConfig().getString("text_commands") + ":");
				HashMap<String, Object> hashmap = product.getValue().getHashMap();
				hashmap.put("player", e.getPlayer().getPlayerName());
				hashmap.put("uuid", e.getPlayer().getUUID().toString());
				for (int i = 0; i < product.getValue().getCommands().size(); i++) {
					getRegistry().logger().log("&7- &b" + mapStringReplace(product.getValue().getCommands().get(i), hashmap));
				}
				getRegistry().logger().log("&7-------------------------------------------");
			}
		}
	}

}
