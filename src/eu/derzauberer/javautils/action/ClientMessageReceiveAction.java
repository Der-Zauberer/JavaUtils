package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ClientMessageRecieveEvent;

public interface ClientMessageReceiveAction {
	
	public abstract void onAction(ClientMessageRecieveEvent event);

}
