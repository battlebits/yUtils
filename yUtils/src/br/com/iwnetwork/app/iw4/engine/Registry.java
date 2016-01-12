package br.com.iwnetwork.app.iw4.engine;

import static br.com.iwnetwork.app.iw4.IW4.initConfig;
import br.com.iwnetwork.app.iw4.system.Api;
import br.com.iwnetwork.app.iw4.system.Auth;

/**
 *
 * @author Renato
 */
public class Registry {

    private final Logger logger;
    private final Api api;
    private final Auth auth;

    public Registry() {
        this.logger = new Logger();
        this.api = new Api();
        this.auth = new Auth();
    }
    
    public void init() {
        initConfig();
        
        // Api
        api.init();
    }

    public Logger logger() {
        return this.logger;
    }

    public Api api() {
        return this.api;
    }
    
    public Auth auth() {
        return this.auth;
    }

}
