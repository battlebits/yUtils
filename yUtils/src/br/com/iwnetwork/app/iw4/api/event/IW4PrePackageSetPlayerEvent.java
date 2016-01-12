package br.com.iwnetwork.app.iw4.api.event;

import br.com.iwnetwork.app.iw4.object.IW4Event;
import br.com.iwnetwork.app.iw4.object.IW4Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Renato
 */
public class IW4PrePackageSetPlayerEvent extends Event {

    private final IW4Event event;
    private final IW4Player player;
    
    public IW4PrePackageSetPlayerEvent(IW4Player player, IW4Event event) {
        this.event = event;
        this.player = player;
    }
    
    public IW4Event getEvent() {
        return this.event;
    }
    
    public IW4Player getPlayer() {
        return this.player;
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
