package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.util.Client;

public interface ClientMessageReceiveAction {
	
	public abstract void onAction(Client client, String message);

}
