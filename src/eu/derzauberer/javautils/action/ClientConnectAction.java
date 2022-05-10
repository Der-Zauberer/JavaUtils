package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ClientConnectEvent;

public interface ClientConnectAction {
	
	public abstract void onAction(ClientConnectEvent event);

}
