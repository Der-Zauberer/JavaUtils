package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Client;
import eu.derzauberer.javautils.util.Event;

public class ClientDisconnectEvent extends Event {
	
	public enum DisconnectCause {CLOSED, DISCONNECTED, TIMEOUT}
	
	private Client client;
	private DisconnectCause cause;
	
	public ClientDisconnectEvent(Client client, DisconnectCause cause) {
		this.client = client;
		this.cause = cause;
		execute();
	}
	
	public Client getClient() {
		return client;
	}
	
	public DisconnectCause getCause() {
		return cause;
	}
	
}