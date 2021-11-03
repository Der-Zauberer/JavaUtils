package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Client;
import eu.derzauberer.javautils.util.Event;

public class ClientConnectEvent extends Event {
	
	private Client client;
	
	public ClientConnectEvent(Client client) {
		this.client = client;
	}
	
	public Client getClient() {
		return client;
	}

}