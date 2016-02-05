package br.com.battlebits.iw4.request;

import br.com.battlebits.iw4.engine.Request;
import br.com.battlebits.iw4.json.JSONArray;
import br.com.battlebits.iw4.json.JSONObject;
import br.com.battlebits.iw4.object.IW4Player;
import br.com.battlebits.iw4.object.IW4PlayerPackage;

import static br.com.battlebits.iw4.IW4.getRegistry;

import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author Renato
 */
public class RequestPlayerPackages extends Request {

    public RequestPlayerPackages(HashMap<Object, Object> data) {
        this.data = data;
    }

    public HashMap<Object, IW4PlayerPackage> request() {
        HashMap<Object, IW4PlayerPackage> set_list = new HashMap<>();
        
        IW4Player player = new IW4Player();
        player.setUUID(UUID.fromString((String) this.data.get("uuid")));
        player.setPlayerName((String) this.data.get("player"));
        
        JSONObject response = getRegistry().api().getPlayerPackages(this.data);
        if ("data:success".equals(response.getString("status"))) {
            JSONArray added_packages = response.getJSONArray("response");

            for (int i = 0; i < added_packages.length(); i++) {
                JSONObject item = added_packages.getJSONObject(i);
                HashMap<String, Object> item_data = new HashMap<>();

                item_data.put("minecraft_account_package_id", item.getInt("minecraft_account_package_id"));
                item_data.put("product_name", item.getString("product_name"));
                item_data.put("days", item.getInt("days"));
                item_data.put("lifetime", item.getBoolean("lifetime"));
                item_data.put("date_added", item.getString("date_added"));
                item_data.put("date_updated", item.getString("date_updated"));

                IW4PlayerPackage order = new IW4PlayerPackage(item_data);
                set_list.put(i, order);
            }

        } else {
            if (response.has("error")) {
                getRegistry().logger().logException(response.getJSONObject("error").getString("warning"));
            }
        }
        return set_list;
    }

}
