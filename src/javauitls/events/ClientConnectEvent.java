package javauitls.events;

import javautils.util.Client;
import javautils.util.Event;

public class ClientConnectEvent extends Event {
	
	private Client client;
	
	public ClientConnectEvent(Client client) {
		this.client = client;
	}
	
	public Client getClient() {
		return client;
	}

}