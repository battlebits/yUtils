package br.com.battlebits.iw4.controller;

import static br.com.battlebits.iw4.IW4.getRegistry;

import java.util.HashMap;

import br.com.battlebits.iw4.engine.Controller;

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
