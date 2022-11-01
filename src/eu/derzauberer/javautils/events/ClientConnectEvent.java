package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.sockets.Client;

public class ClientConnectEvent extends CancellableEvent {
	
	private final Client client;
	
	public ClientConnectEvent(Client client) {
		this.client = client;
	}
	
	public Client getClient() {
		return client;
	}

}