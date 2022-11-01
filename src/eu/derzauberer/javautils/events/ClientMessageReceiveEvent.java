package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.sockets.Client;

public class ClientMessageReceiveEvent extends CancellableEvent {
	
	private final Client client;
	private String message;
	
	public ClientMessageReceiveEvent(Client client, String message) {
		this.client = client;
		this.message = message;
	}
	
	public Client getClient() {
		return client;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
