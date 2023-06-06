package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.service.ClientService;

/**
 * This event gets called when a client disconnected from a server.
 */
public class ClientDisconnectEvent extends Event {
	
	/**
	 * Represents the cause if the disconnect.
	 */
	public enum DisconnectCause {CLOSED, DISCONNECTED, TIMEOUT}
	
	private final ClientService client;
	private final DisconnectCause cause;
	
	/**
	 * Creates a new event which is called when a client disconnected from a server
	 * 
	 * @param client the client which disconnected from the server
	 * @param cause the cause of the disconnect
	 */
	public ClientDisconnectEvent(ClientService client, DisconnectCause cause) {
		this.client = client;
		this.cause = cause;
	}
	
	/**
	 * Returns the client which disconnected from the server.
	 * 
	 * @return the client which disconnected from the server
	 */
	public ClientService getClient() {
		return client;
	}
	
	/**
	 * Returns the cause if the disconnect.
	 * 
	 * @return the cause if the disconnect
	 */
	public DisconnectCause getCause() {
		return cause;
	}
	
}