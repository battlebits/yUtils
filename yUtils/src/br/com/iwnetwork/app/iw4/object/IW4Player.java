package br.com.iwnetwork.app.iw4.object;

import java.util.UUID;

/**
 *
 * @author Renato
 */
public final class IW4Player {
    
    private UUID uuid;
    private String playername;
    
    public IW4Player() {}
    
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
    
    public void setPlayerName(String playername) {
        this.playername = playername;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public String getPlayerName() {
        return this.playername;
    }
    
}
