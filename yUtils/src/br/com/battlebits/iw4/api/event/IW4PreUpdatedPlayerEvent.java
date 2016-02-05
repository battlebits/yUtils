package br.com.battlebits.iw4.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.battlebits.iw4.object.IW4Player;

/**
 *
 * @author Renato
 */
public class IW4PreUpdatedPlayerEvent extends Event {

    private final IW4Player player;
    
    public IW4PreUpdatedPlayerEvent(IW4Player player) {
        this.player = player;
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
