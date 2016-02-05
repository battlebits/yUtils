package br.com.battlebits.iw4.api;

import static br.com.battlebits.iw4.IW4.getRegistry;

/**
 *
 * @author Renato
 */
public class IW4Api {
    
    public boolean isLogged() {
        return getRegistry().auth().isLogged();
    }
    
    public boolean login() {
        return getRegistry().auth().login();
    }
    
}
