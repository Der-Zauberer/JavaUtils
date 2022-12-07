package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.controller.ClientController;

/**
 * This event gets called when the client sends a message.
 */
public class ClientMessageSendEvent extends CancellableEvent {

	private final ClientController client;
	private String message;

	/**
	 * Creates a new event, which gets called when the client sends a message.
	 * 
	 * @param client  the client which sends the message
	 * @param message the received message
	 */
	public ClientMessageSendEvent(ClientController client, String message) {
		this.client = client;
		this.message = message;
	}
	
	/**
	 * Returns the client sending the message.
	 * 
	 * @return the client sending the message
	 */
	public ClientController getClient() {
		return client;
	}
	
	/**
	 * Sets the message that will be sent.
	 * 
	 * @param message the message that will be sent
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Returns the message that will be sent.
	 * 
	 * @return the message that will be sent
	 */
	public String getMessage() {
		return message;
	}

}
