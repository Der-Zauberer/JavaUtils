package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Client;
import eu.derzauberer.javautils.util.Event;

public class ClientMessageRecieveEvent extends Event {
	
	private boolean cancled;
	private Client client;
	private String message;
	
	public ClientMessageRecieveEvent(Client client, String message) {
		this.cancled = false;
		this.client = client;
		this.message = message;
	}
	
	public void setCancled(boolean cancled) {
		this.cancled = cancled;
	}
	
	public boolean isCancled() {
		return cancled;
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