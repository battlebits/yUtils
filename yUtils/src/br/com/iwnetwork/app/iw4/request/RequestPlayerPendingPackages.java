package br.com.iwnetwork.app.iw4.request;

import static br.com.iwnetwork.app.iw4.IW4.getPm;
import static br.com.iwnetwork.app.iw4.IW4.getRegistry;
import br.com.iwnetwork.app.iw4.api.event.IW4PostPendingPlayerEvent;
import br.com.iwnetwork.app.iw4.api.event.IW4PrePendingPlayerEvent;
import br.com.iwnetwork.app.iw4.engine.Request;
import br.com.iwnetwork.app.iw4.json.JSONArray;
import br.com.iwnetwork.app.iw4.json.JSONObject;
import br.com.iwnetwork.app.iw4.object.IW4OrderProduct;
import br.com.iwnetwork.app.iw4.object.IW4Player;
import static br.com.iwnetwork.app.iw4.system.Functions.jArrayToHashMap;
import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author Renato
 */
public class RequestPlayerPendingPackages extends Request {

    public RequestPlayerPendingPackages(HashMap<Object, Object> data) {
        this.data = data;
    }

    public HashMap<Object, IW4OrderProduct> request() {
        HashMap<Object, IW4OrderProduct> po_list = new HashMap<>();
            
        IW4Player player = new IW4Player();
        player.setUUID(UUID.fromString((String) this.data.get("uuid")));
        player.setPlayerName((String) this.data.get("player"));
        
        getPm().callEvent(new IW4PrePendingPlayerEvent(player));

        JSONObject response = getRegistry().api().getPlayerPendingOrders(this.data);
        if ("data:success".equals(response.getString("status"))) {
            JSONArray pending_orders = response.getJSONArray("response");
            
            for (int i = 0; i < pending_orders.length(); i++) {
                JSONObject item = pending_orders.getJSONObject(i);
                HashMap<String, Object> item_data = new HashMap<>();
                
                JSONArray cmd = item.getJSONArray("commands");
                HashMap<Integer, String> commands = jArrayToHashMap(cmd);
                
                boolean lifetime = false;
                if (item.getInt("lifetime") == 1) {
                    lifetime = true;
                }
                
                item_data.put("pending_id", item.getInt("pending_id"));
                item_data.put("product_id", item.getInt("product_id"));
                item_data.put("product_name", item.getString("product_name"));
                item_data.put("product_group", item.getInt("product_group"));
                item_data.put("product_quantity", item.getInt("product_quantity"));
                item_data.put("duration_single", item.getInt("duration_single"));
                item_data.put("duration", item.getInt("duration"));
                item_data.put("lifetime", lifetime);
                item_data.put("commands", commands);
                item_data.put("event", item.getString("event"));
                
                IW4OrderProduct order = new IW4OrderProduct(item_data);
                po_list.put(i, order);
            }
            if (!po_list.isEmpty()) {
                getPm().callEvent(new IW4PostPendingPlayerEvent(player, po_list));
            }
        } else {
            if (response.has("error")) {
                getRegistry().logger().logException(response.getJSONObject("error").getString("warning"));
            }
        }
        return po_list;
    }

}
