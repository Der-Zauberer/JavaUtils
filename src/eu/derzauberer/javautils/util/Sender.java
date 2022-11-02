package eu.derzauberer.javautils.util;

import java.lang.management.ManagementFactory;

/**
 * Represents an object that can send and receive messages.
 */
public interface Sender {
	
	/**
	 * Represents the type of the message.
	 */
	enum MessageType {DEFAULT, DEBUG, INFO, SUCCESS, WARNING, ERROR}
	
	/**
	 * The method gets called, when the sender receives a massage. Please do not
	 * call this method by yourself, it is reserved for internal usage.
	 * 
	 * @param input the raw input that the sender receives
	 */
	void sendInput(String input);
	
	/**
	 * The method gets called, when the sender sends a message. Please do not call
	 * this method by yourself, it is reserved for internal usage. Use
	 * {@link #sendMessage(String)} instead, which calls implicitly this method.
	 * 
	 * @param input the raw input that the sender receives
	 */
	void sendOutput(String message, MessageType type);
	
	/**
	 * Sends a simple message to the sender.
	 * 
	 * @param message the message to send
	 */
	default void sendMessage(String message) {
		sendMessage(getDefaultMessageType(), message);
	}
	
	/**
	 * Sends a simple message to the sender.
	 * 
	 * @param message the message to send
	 * @param args    arguments to insert in the message with the
	 *                build in {@link String#format(String, Object...)} method
	 */
	default void sendMessage(String message, String... args) {
		sendMessage(getDefaultMessageType(), message, args);
	}

	/**
	 * Sends a simple message to the sender.
	 * 
	 * @param type    which type the message should be
	 * @param message the message to send
	 */
	default void sendMessage(MessageType type, String message) {
		if (type != MessageType.DEBUG || isDebugEnabled()) {
			String output;
			if (type == MessageType.DEFAULT)
				output = message;
			else
				output = "[" + type.toString() + "] " + message;
			sendOutput(output, type);
		}
	}

	/**
	 * Sends a simple message to the sender.
	 * 
	 * @param type    which type the message should be
	 * @param message the message to send
	 * @param args    arguments to insert in the message with the
	 *                build in {@link String#format(String, Object...)} method
	 */
	default void sendMessage(MessageType type, String message, String... args) {
		sendMessage(type, String.format(message, (Object[]) args));
	}
	
	/**
	 * Returns if java is currently running in debug mode.
	 * 
	 * @return if java is currently running in debug mode
	 */
	default boolean isDebugEnabled() {
		return ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}
	
	/**
	 * Sets the default message type of the sender.
	 * 
	 * @param type the default message type of the sender
	 */
	void setDefaultMessageType(MessageType type);
	
	/**
	 * Returns the default message type of the sender.
	 * 
	 * @return the default message type of the sender
	 */
	MessageType getDefaultMessageType();

}
