package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.sockets.Client;

public class ClientDisconnectEvent extends Event {
	
	public enum DisconnectCause {CLOSED, DISCONNECTED, TIMEOUT}
	
	private final Client client;
	private final DisconnectCause cause;
	
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