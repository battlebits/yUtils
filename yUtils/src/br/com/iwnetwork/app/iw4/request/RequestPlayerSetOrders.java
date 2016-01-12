package br.com.iwnetwork.app.iw4.request;

import static br.com.iwnetwork.app.iw4.IW4.getPm;
import static br.com.iwnetwork.app.iw4.IW4.getRegistry;
import br.com.iwnetwork.app.iw4.api.event.IW4PostPackageSetPlayerEvent;
import br.com.iwnetwork.app.iw4.api.event.IW4PrePackageSetPlayerEvent;
import br.com.iwnetwork.app.iw4.engine.Request;
import br.com.iwnetwork.app.iw4.json.JSONArray;
import br.com.iwnetwork.app.iw4.json.JSONObject;
import br.com.iwnetwork.app.iw4.object.IW4Event;
import br.com.iwnetwork.app.iw4.object.IW4OrderProduct;
import br.com.iwnetwork.app.iw4.object.IW4Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Renato
 */
public class RequestPlayerSetOrders extends Request {

    public RequestPlayerSetOrders(HashMap<Object, Object> data) {
        this.data = data;
    }

    public HashMap<Object, IW4OrderProduct> request(HashMap<Object, IW4OrderProduct> po_list) {
        HashMap<Object, IW4OrderProduct> set_list = new HashMap<>();
        
        IW4Player player = new IW4Player();
        player.setUUID(UUID.fromString((String) this.data.get("uuid")));
        player.setPlayerName((String) this.data.get("player"));
        
        IW4Event event = new IW4Event((String) this.data.get("event"));
        
        getPm().callEvent(new IW4PrePackageSetPlayerEvent(player, event));

        JSONArray pending_list = new JSONArray();
        for (Map.Entry<Object, IW4OrderProduct> entry : po_list.entrySet()) {
            pending_list.put(entry.getValue().getPendingId());
        }

        this.data.put("pending_list", pending_list.toString());
        
        JSONObject response = getRegistry().api().setOrders(this.data);
        if ("data:success".equals(response.getString("status"))) {
            JSONArray added_packages = response.getJSONArray("response");

            for (int i = 0; i < added_packages.length(); i++) {
                JSONObject item = added_packages.getJSONObject(i);
                HashMap<String, Object> item_data = new HashMap<>();

                item_data.put("product_id", item.getInt("product_id"));
                item_data.put("product_name", item.getString("product_name"));
                item_data.put("event_name", item.getString("event"));

                IW4OrderProduct order = new IW4OrderProduct(item_data);
                set_list.put(i, order);
            }
            if (!set_list.isEmpty()) {
                getPm().callEvent(new IW4PostPackageSetPlayerEvent(player, set_list, event));
            }
        } else {
            if (response.has("error")) {
                getRegistry().logger().logException(response.getJSONObject("error").getString("warning"));
            }
        }
        return set_list;
    }

}
