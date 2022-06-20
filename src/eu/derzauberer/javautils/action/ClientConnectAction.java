package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ClientConnectEvent;

public interface ClientConnectAction {
	
	void onAction(ClientConnectEvent event);

}
