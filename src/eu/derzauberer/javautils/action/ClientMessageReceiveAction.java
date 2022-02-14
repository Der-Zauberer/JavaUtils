package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ClientMessageReceiveEvent;

public interface ClientMessageReceiveAction {
	
	public abstract void onAction(ClientMessageReceiveEvent event);

}
