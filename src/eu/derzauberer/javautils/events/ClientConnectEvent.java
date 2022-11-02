package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.controller.ClientController;

/**
 * This event gets called, when a client connected to a server.
 */
public class ClientConnectEvent extends Event {

	private final ClientController client;

	/**
	 * Creates a new event, which is called when a client connected to a server
	 * 
	 * @param client the client which connected to the server
	 */
	public ClientConnectEvent(ClientController client) {
		this.client = client;
	}

	/**
	 * Returns the client connecting to the server.
	 * 
	 * @return the client connecting to the server
	 */
	public ClientController getClient() {
		return client;
	}

}