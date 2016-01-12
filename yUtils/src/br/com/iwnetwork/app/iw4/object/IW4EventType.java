package br.com.iwnetwork.app.iw4.object;

/**
 *
 * @author Renato
 */
public final class IW4EventType {
    
    private final String event;
    
    public IW4EventType(String event) {
        this.event = event;
    }
    
    public String getEventName() {
        return this.event;
    }
    
    public boolean onStart() {
        return ("onstart".equals(this.event));
    }
    
    public boolean onStop() {
        return ("onstop".equals(this.event));
    }
    
}
