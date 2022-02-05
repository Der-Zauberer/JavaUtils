package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Client;
import eu.derzauberer.javautils.util.Event;

public class ClientMessageRecieveEvent extends Event {
	
	private Client client;
	private String message;
	
	public ClientMessageRecieveEvent(Client client, String message) {
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
