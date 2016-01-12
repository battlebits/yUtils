package br.com.iwnetwork.app.iw4.request;

import static br.com.iwnetwork.app.iw4.IW4.getPm;
import static br.com.iwnetwork.app.iw4.IW4.getRegistry;

import java.util.HashMap;
import java.util.UUID;

import br.com.iwnetwork.app.iw4.api.event.IW4PostExpiredPlayerEvent;
import br.com.iwnetwork.app.iw4.api.event.IW4PostUpdatedPlayerEvent;
import br.com.iwnetwork.app.iw4.api.event.IW4PreExpiredPlayerEvent;
import br.com.iwnetwork.app.iw4.api.event.IW4PreUpdatedPlayerEvent;
import br.com.iwnetwork.app.iw4.engine.Request;
import br.com.iwnetwork.app.iw4.json.JSONArray;
import br.com.iwnetwork.app.iw4.json.JSONObject;
import br.com.iwnetwork.app.iw4.object.IW4OrderProduct;
import br.com.iwnetwork.app.iw4.object.IW4Player;

/**
 *
 * @author Renato
 */

public class RequestPlayerExpiredPackages extends Request {

	private final IW4Player player;
	private final HashMap<Object, IW4OrderProduct> updated;
	private final HashMap<Object, IW4OrderProduct> expired;

	public RequestPlayerExpiredPackages(HashMap<Object, Object> data) {
		this.data = data;
		this.updated = new HashMap<>();
		this.expired = new HashMap<>();
		this.player = new IW4Player();

		this.player.setUUID(UUID.fromString((String) this.data.get("uuid")));
		this.player.setPlayerName((String) this.data.get("player"));
	}

	public void request() {

		getPm().callEvent(new IW4PreExpiredPlayerEvent(player));
		getPm().callEvent(new IW4PreUpdatedPlayerEvent(player));

		JSONObject response = getRegistry().api().getPlayerExpiredOrders(this.data);
		if ("data:success".equals(response.getString("status"))) {
			JSONObject response_data = response.getJSONObject("response");

			JSONArray upd = response_data.getJSONArray("updated");
			JSONArray rvd = response_data.getJSONArray("removed");

			for (int i = 0; i < upd.length(); i++) {
				JSONObject item = upd.getJSONObject(i);
				HashMap<String, Object> item_data = new HashMap<>();

				item_data.put("product_id", item.getInt("product_id"));
				item_data.put("product_name", item.getString("product_name"));

				IW4OrderProduct order = new IW4OrderProduct(item_data);
				updated.put(i, order);
			}
			for (int i = 0; i < rvd.length(); i++) {
				JSONObject item = rvd.getJSONObject(i);
				HashMap<String, Object> item_data = new HashMap<>();

				item_data.put("product_id", item.getInt("product_id"));
				item_data.put("product_name", item.getString("product_name"));

				IW4OrderProduct order = new IW4OrderProduct(item_data);
				expired.put(i, order);
			}
		} else {
			if (response.has("error")) {
				getRegistry().logger().logException(response.getJSONObject("error").getString("warning"));
			}
		}
	}

	public HashMap<Object, IW4OrderProduct> getUpdated() {
		if (!this.updated.isEmpty()) {
			getPm().callEvent(new IW4PostUpdatedPlayerEvent(player, this.updated));
		}
		return this.updated;
	}

	public HashMap<Object, IW4OrderProduct> getExpired() {
		if (!this.expired.isEmpty()) {
			getPm().callEvent(new IW4PostExpiredPlayerEvent(player, this.expired));
		}
		return this.expired;
	}

}
