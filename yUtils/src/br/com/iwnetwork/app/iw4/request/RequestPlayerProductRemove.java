package br.com.iwnetwork.app.iw4.request;

import static br.com.iwnetwork.app.iw4.IW4.getPlugin;
import static br.com.iwnetwork.app.iw4.IW4.getPm;
import static br.com.iwnetwork.app.iw4.IW4.getRegistry;
import static br.com.iwnetwork.app.iw4.system.Functions.mapStringReplace;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.iwnetwork.app.iw4.api.event.IW4PostPlayerProductRemoveEvent;
import br.com.iwnetwork.app.iw4.api.event.IW4PrePlayerProductRemoveEvent;
import br.com.iwnetwork.app.iw4.engine.Request;
import br.com.iwnetwork.app.iw4.object.IW4OrderProduct;
import br.com.iwnetwork.app.iw4.object.IW4Player;

/**
 *
 * @author Renato
 */
public class RequestPlayerProductRemove extends Request {

	public RequestPlayerProductRemove(HashMap<Object, Object> data) {
		this.data = data;
	}

	@SuppressWarnings("unchecked")
	public void request(IW4OrderProduct product) {

		IW4Player player = new IW4Player();
		player.setUUID(UUID.fromString((String) this.data.get("uuid")));
		player.setPlayerName((String) this.data.get("player"));

		getPm().callEvent(new IW4PrePlayerProductRemoveEvent(player, product));
		HashMap<String, Object> hashmap = (HashMap<String, Object>) product.getHashMap().clone();
		hashmap.put("player", player.getPlayerName());
		hashmap.put("uuid", player.getUUID().toString());
		hashmap.put("product", product.getProductName());
		hashmap.put("quantity", String.valueOf(product.getProductQuantity()));
		hashmap.put("duration_single", String.valueOf(product.getDurationSingle()));
		hashmap.put("duration", String.valueOf(product.getDuration()));
		hashmap.put("lifetime", String.valueOf(product.getLifetime()));

		// Execute commands
		HashMap<Integer, String> commands = product.getCommands();
		for (Map.Entry<Integer, String> command : commands.entrySet()) {
			String cmd = command.getValue();
			if (cmd.startsWith("/")) {
				cmd = cmd.substring(1);
			}
			cmd = mapStringReplace(cmd, hashmap);
			getRegistry().logger().log("&c[!] Executing: &7" + cmd + " &c...");
			getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(), cmd);
		}

		getPm().callEvent(new IW4PostPlayerProductRemoveEvent(player, product));
	}

}