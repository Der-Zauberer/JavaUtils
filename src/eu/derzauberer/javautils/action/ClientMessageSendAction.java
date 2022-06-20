package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ClientMessageSendEvent;

public interface ClientMessageSendAction {

	void onAction(ClientMessageSendEvent event);
	
}
