package br.com.battlebits.iw4.object;

/**
 *
 * @author Renato
 */
public final class IW4Event {
    
    private final IW4EventType eventtype;
    
    public IW4Event(String event) {
        this.eventtype = new IW4EventType(event);
    }
    
    public IW4EventType getEventType() {
        return this.eventtype;
    }
    
}
