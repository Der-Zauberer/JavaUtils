package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ClientDisconnectEvent;

public interface ClientDisconnectAction {
	
	void onAction(ClientDisconnectEvent event);

}
