package br.com.iwnetwork.app.iw4.api.event;

import br.com.iwnetwork.app.iw4.object.IW4OrderProduct;
import br.com.iwnetwork.app.iw4.object.IW4Player;
import java.util.HashMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Renato
 */
public class IW4PostExpiredPlayerEvent extends Event {

    private final IW4Player player;
    private final HashMap<Object, IW4OrderProduct> orders;

    public IW4PostExpiredPlayerEvent(IW4Player player, HashMap<Object, IW4OrderProduct> pending_orders) {
        this.player = player;
        this.orders = pending_orders;
    }

    public IW4Player getPlayer() {
        return this.player;
    }

    public HashMap<Object, IW4OrderProduct> getExpiredPackages() {
        return orders;
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
