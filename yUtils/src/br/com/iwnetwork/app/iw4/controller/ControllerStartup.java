package br.com.iwnetwork.app.iw4.controller;

import static br.com.iwnetwork.app.iw4.IW4.getRegistry;
import br.com.iwnetwork.app.iw4.engine.Controller;
import java.util.HashMap;

/**
 *
 * @author Renato
 */
public class ControllerStartup extends Controller {

    public void index(HashMap<Object, Object> data) {
        this.data = data;
    }

    public void init() {
        getRegistry().auth().login();
        if (!getRegistry().auth().isLogged()) {
            getRegistry().logger().logException("[IW4] API Connection Failure");
        }
    }

}
