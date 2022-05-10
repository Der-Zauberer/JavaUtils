package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ClientMessageSendEvent;

public interface ClientMessageSendAction {

	public abstract void onAction(ClientMessageSendEvent event);
	
}
