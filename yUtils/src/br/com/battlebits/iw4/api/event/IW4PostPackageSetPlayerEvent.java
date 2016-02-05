package br.com.battlebits.iw4.api.event;

import java.util.HashMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.battlebits.iw4.object.IW4Event;
import br.com.battlebits.iw4.object.IW4OrderProduct;
import br.com.battlebits.iw4.object.IW4Player;

/**
 *
 * @author Renato
 */
public class IW4PostPackageSetPlayerEvent extends Event {

    private final IW4Event event;
    private final IW4Player player;
    private final HashMap<Object, IW4OrderProduct> orders;
    
    public IW4PostPackageSetPlayerEvent(IW4Player player, HashMap<Object, IW4OrderProduct> added_packages, IW4Event event) {
        this.event = event;
        this.player = player;
        this.orders = added_packages;
    }
    
    public IW4Event getEvent() {
        return this.event;
    }
    
    public IW4Player getPlayer() {
        return this.player;
    }
    
    public HashMap<Object, IW4OrderProduct> getAddedPackages() {
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
