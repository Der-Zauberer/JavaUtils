package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ClientMessageReceiveEvent;

public interface ClientMessageReceiveAction {
	
	void onAction(ClientMessageReceiveEvent event);

}
