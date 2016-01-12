package br.com.iwnetwork.app.iw4.api.event;

import br.com.iwnetwork.app.iw4.object.IW4OrderProduct;
import br.com.iwnetwork.app.iw4.object.IW4Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Renato
 */
public class IW4PostPlayerProductAddEvent extends Event {

    private final IW4Player player;
    private final IW4OrderProduct product;
    
    public IW4PostPlayerProductAddEvent(IW4Player player, IW4OrderProduct product) {
        this.player = player;
        this.product = product;
    }
    
    public IW4Player getPlayer() {
        return this.player;
    }
    
    public IW4OrderProduct getProduct() {
        return product;
    }
    
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
