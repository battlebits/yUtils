package br.com.battlebits.iw4.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.battlebits.iw4.object.IW4OrderProduct;
import br.com.battlebits.iw4.object.IW4Player;

/**
 *
 * @author Renato
 */
public class IW4PrePlayerProductRemoveEvent extends Event {

    private final IW4Player player;
    private final IW4OrderProduct product;
    
    public IW4PrePlayerProductRemoveEvent(IW4Player player, IW4OrderProduct product) {
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
