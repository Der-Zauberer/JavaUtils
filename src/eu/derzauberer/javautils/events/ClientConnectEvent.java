package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.sockets.Client;

public class ClientConnectEvent extends Event {
	
	private final Client client;
	
	public ClientConnectEvent(Client client) {
		this.client = client;
		execute();
	}
	
	public Client getClient() {
		return client;
	}

}