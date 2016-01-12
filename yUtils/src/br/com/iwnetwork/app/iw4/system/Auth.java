package br.com.iwnetwork.app.iw4.system;

import static br.com.iwnetwork.app.iw4.IW4.getRegistry;
import br.com.iwnetwork.app.iw4.json.JSONObject;

/**
 *
 * @author Renato
 */
public class Auth {

    public boolean isLogged() {
        JSONObject response = getRegistry().api().chkLoginAction();
        
        return response.getBoolean("logged");
    }

    public boolean login() {
        JSONObject response = getRegistry().api().authLoginAction();

        if ("data:success".equals(response.getString("status"))) {
            if (response.getJSONObject("response").has("token")) {
                if (response.getJSONObject("response").has("description")) {
                    getRegistry().logger().log(response.getJSONObject("response").getString("description"));
                }
                getRegistry().api().setToken(response.getJSONObject("response").getString("token"));
                return true;
            }
        } else {
            if (response.has("error")) {
                getRegistry().logger().logException(response.getJSONObject("error").getString("warning"));
            }
        }
        return false;
    }

}
