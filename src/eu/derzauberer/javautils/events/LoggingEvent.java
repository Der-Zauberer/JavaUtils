package eu.derzauberer.javautils.events;

import java.time.LocalDateTime;
import eu.derzauberer.javautils.controller.LoggingController;
import eu.derzauberer.javautils.controller.LoggingController.LogType;

/**
 * The event gets called when one of the {@link LoggingController#log(String)} functions was used in the {@link LoggingController}.
 */
public class LoggingEvent extends Event {
	
	private final LoggingController logger;
	private final LogType type;
	private final String message;
	private final LocalDateTime timeStamp;
	private final String output;
	
	/**
	 * Creates a new event that gets called when one of the
	 * {@link LoggingController#log(String)} functions was used in the
	 * {@link LoggingController}.
	 * 
	 * @param logger    the logger from which the event was triggered
	 * @param type      the type of the message
	 * @param message   the message that was send
	 * @param timeStamp the timestamp when the logger got the message
	 * @param output    the final output which contains the timestamp the type and
	 *                  the message
	 */
	public LoggingEvent(LoggingController logger, LogType type, String message, LocalDateTime timeStamp, String output) {
		this.logger = logger;
		this.type = type;
		this.message = message;
		this.timeStamp = timeStamp;
		this.output = output;
	}

	/**
	 * Returns the logger from which the event was triggered.
	 * 
	 * @return the logger from which the event was triggered
	 */
	public LoggingController getLogger() {
		return logger;
	}
	
	/**
	 * Returns the type of the message.
	 * 
	 * @return the type of the message
	 */
	public LogType getType() {
		return type;
	}

	/**
	 * Returns the message that was send
	 * 
	 * @return the message that was send
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the timestamp when the logger got the message.
	 * 
	 * @return the timestamp when the logger got the message
	 */
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Returns the final output which contains the timestamp the type and the
	 * message.
	 * 
	 * @return the final output which contains the timestamp the type and the
	 *         message
	 */
	public String getOutput() {
		return output;
	}

}