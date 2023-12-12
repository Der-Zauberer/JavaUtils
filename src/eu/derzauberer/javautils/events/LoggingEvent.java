package eu.derzauberer.javautils.events;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import eu.derzauberer.javautils.services.LoggingService;
import eu.derzauberer.javautils.services.LoggingService.LogType;

/**
 * The event gets called when one of the {@link LoggingService#log(String)} functions was used in the {@link LoggingService}.
 */
public class LoggingEvent extends Event {
	
	private final LoggingService logger;
	private final Function<LoggingEvent, String> formatter;
	private final LogType type;
	private final Optional<String> prefix;
	private final LocalDateTime timeStamp;
	private final String message;
	
	/**
	 * Creates a new event that gets called when one of the
	 * {@link LoggingService#log(String)} functions was used in the
	 * {@link LoggingService}.
	 * 
	 * @param logger    the logger from which the event was triggered
	 * @param formatter the formatter to display time, type and prefix in front of
	 *                  the message
	 * @param type      the type of the message
	 * @param prefix    the prefix (ex. the class name) in front of the message
	 * @param message   the message that was sent
	 * @param timeStamp the timestamp when the logger got the message
	 */
	public LoggingEvent(LoggingService logger, Function<LoggingEvent, String> formatter, LogType type, LocalDateTime timeStamp, Optional<String> prefix, String message) {
		this.logger = logger;
		this.formatter = formatter;
		this.type = type;
		this.prefix = prefix;
		this.message = message;
		this.timeStamp = timeStamp;
	}

	/**
	 * Returns the logger from which the event was triggered.
	 * 
	 * @return the logger from which the event was triggered
	 */
	public LoggingService getLogger() {
		return logger;
	}
	
	/**
	 * Returns the the formatter to display time, type and prefix in front of the message.
	 * 
	 * @return the the formatter to display time, type and prefix in front of the message
	 */
	public Function<LoggingEvent, String> getFormatter() {
		return formatter;
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
	 * Returns the prefix of the message.
	 * 
	 * @return the prefix of the message
	 */
	public Optional<String> getPrefix() {
		return prefix;
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
	 * Returns the message that was sent
	 * 
	 * @return the message that was sent
	 */
	public String getMessage() {
		return message;
	}

}
