package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.sockets.Client;

public class ClientMessageReceiveEvent extends Event {
	
	private Client client;
	private String message;
	
	public ClientMessageReceiveEvent(Client client, String message) {
		this.client = client;
		this.message = message;
		execute();
	}
	
	public void setCancled(boolean cancelled) {
		this.setCancelled(cancelled);
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
