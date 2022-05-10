package eu.derzauberer.javautils.accessible;

import eu.derzauberer.javautils.events.ClientDisconnectEvent;

public interface ClientDisconnectAction {
	
	public abstract void onAction(ClientDisconnectEvent event);

}
