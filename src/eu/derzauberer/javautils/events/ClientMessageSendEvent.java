package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.sockets.Client;

public class ClientMessageSendEvent extends Event {
	
	private final Client client;
	private String message;
	
	public ClientMessageSendEvent(Client client, String message) {
		this.client = client;
		this.message = message;
		execute();
	}
	
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
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
