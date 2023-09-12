package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.services.ClientService;

/**
 * This event gets called when the client receives a massage.
 */
public class ClientMessageReceiveEvent extends CancellableEvent {
	
	private final ClientService client;
	private String message;

	/**
	 * Creates a new event, which gets called when the client receives a massage.
	 * 
	 * @param client  the client which receives the message
	 * @param message the received message
	 */
	public ClientMessageReceiveEvent(ClientService client, String message) {
		this.client = client;
		this.message = message;
	}

	/**
	 * Returns the client receiving the message.
	 * 
	 * @return the client receiving the message
	 */
	public ClientService getClient() {
		return client;
	}

	/**
	 * Sets the message that was received.
	 * 
	 * @param message the message that was received
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the message that was received.
	 * 
	 * @return the message that was received
	 */
	public String getMessage() {
		return message;
	}

}
