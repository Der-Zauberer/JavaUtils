package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.services.ClientService;

/**
 * This event gets called when a client connected to a server.
 */
public class ClientConnectEvent extends Event {

	private final ClientService client;

	/**
	 * Creates a new event which is called when a client connected to a server
	 * 
	 * @param client the client which connected to the server
	 */
	public ClientConnectEvent(ClientService client) {
		this.client = client;
	}

	/**
	 * Returns the client connecting to the server.
	 * 
	 * @return the client connecting to the server
	 */
	public ClientService getClient() {
		return client;
	}

}