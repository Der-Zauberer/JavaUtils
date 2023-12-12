package eu.derzauberer.javautils.services;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.derzauberer.javautils.events.LoggingEvent;

/**
 * The logger collects messages from the {@link #log(String)} methods and send
 * the output with date, type and message to the console and writes it in a
 * logging file if enabled. You can enable the options with
 * {@link #setSystemOutput(boolean)} and {@link #setFileOutput(boolean)}. The
 * use of one of the {@link #log(String)} functions triggers a
 * {@link LoggingEvent}.
 */
public class LoggingService {
	
	/**
	 * Represents the type of logging outputs.
	 */
	public enum LogType {
		INFO,
		SUCCESS,
		WARN,
		ERROR,
		DEBUG
	}
	
	private final String prefix;
	private boolean systemOutput = true;
	private boolean fileOutput = true;
	private boolean debug = false;
	private Path fileDirectory = Path.of("logs");
	private Consumer<LoggingEvent> loggingAction;

	private Function<LoggingEvent, String> formatter = (event) -> {
		final StringBuilder string = new StringBuilder();
		string.append("[");
		string.append(event.getTimeStamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
		string.append(" ");
		string.append(event.getType());
		string.append("] ");
		event.getPrefix().ifPresent(prefix -> string.append(prefix + ": "));
		string.append(event.getMessage());
		return string.toString();
	};
	
	/**
	 *  Creates a new logging service without a prefix.
	 */
	public LoggingService() {
		prefix = null;
	}
	
	/**
	 * Creates a new logging service with a prefix. The prefix will be displayed
	 * between the logging type and the message.
	 * 
	 * @param prefix the prefix that will be displayed between the logging type and
	 *               the message
	 */
	public LoggingService(String  prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Creates a new logging service with the name of the class as prefix. The prefix will be displayed
	 * between the logging type and the message.
	 * 
	 * @param classType the class of which the name will be displayed as prefix between the logging type and
	 *               the message
	 */
	public LoggingService(Class<?> classType) {
		prefix = classType.getName();
	}
	
	/**
	 * Sends an information message to the logger.
	 * 
	 * @param message the message to send
	 */
	public void info(String message) {
		log(LogType.INFO, message);
	}
	
	/**
	 * Sends an information message with arguments to the logger.
	 * 
	 * @param message the message to send
	 * @param args    arguments to insert in the message with the build in
	 *                {@link String#format(String, Object...)} method
	 */
	public void info(String message, Object... args) {
		log(LogType.INFO, message, args);
	}
	
	/**
	 * Sends an information message with a string supplier to the logger.
	 * 
	 * @param message supplier that builds the string output when it's called
	 */
	public void info(Supplier<String> action) {
		log(LogType.INFO, action);
	}
	
	/**
	 * Sends a success message to the logger.
	 * 
	 * @param message the message to send
	 */
	public void success(String message) {
		log(LogType.SUCCESS, message);
	}
	
	/**
	 * Sends a success message with arguments to the logger.
	 * 
	 * @param message the message to send
	 * @param args    arguments to insert in the message with the build in
	 *                {@link String#format(String, Object...)} method
	 */
	public void success(String message, Object... args) {
		log(LogType.SUCCESS, message, args);
	}
	
	/**
	 * Sends a success message with a string supplier to the logger.
	 * 
	 * @param message supplier that builds the string output when it's called
	 */
	public void success(Supplier<String> message) {
		log(LogType.SUCCESS, message);
	}
	
	/**
	 * Sends a warn message to the logger.
	 * 
	 * @param message the message to send
	 */
	public void warn(String message) {
		log(LogType.WARN, message);
	}
	
	/**
	 * Sends a warn message with arguments to the logger.
	 * 
	 * @param message the message to send
	 * @param args    arguments to insert in the message with the build in
	 *                {@link String#format(String, Object...)} method
	 */
	public void warn(String message, Object... args) {
		log(LogType.WARN, message, args);
	}
	
	/**
	 * Sends a warn message with a string supplier to the logger.
	 * 
	 * @param message supplier that builds the string output when it's called
	 */
	public void warn(Supplier<String> message) {
		log(LogType.WARN, message);
	}
	
	/**
	 * Sends an error message to the logger.
	 * 
	 * @param message the message to send
	 */
	public void error(String message) {
		log(LogType.ERROR, message);
	}
	
	/**
	 * Sends an error message with arguments to the logger.
	 * 
	 * @param message the message to send
	 * @param args    arguments to insert in the message with the build in
	 *                {@link String#format(String, Object...)} method
	 */
	public void error(String message, Object... args) {
		log(LogType.ERROR, message);
	}
	
	/**
	 * Sends an error message with a string supplier to the logger.
	 * 
	 * @param message supplier that builds the string output when it's called
	 */
	public void error(Supplier<String> message) {
		log(LogType.ERROR, message);
	}
	
	/**
	 * Sends a debug message to the logger. The method does noting if logging is
	 * disabled.
	 * 
	 * @param message the message to send
	 */
	public void debug(String message) {
		if (!isDebugEnabled()) return;
		log(LogType.DEBUG, message);
	}
	
	/**
	 * Sends a debug message with arguments to the logger. The method does noting if
	 * logging is disabled. The string format will not be processed if the logging
	 * is disabled.
	 * 
	 * @param message the message to send
	 * @param args    arguments to insert in the message with the build in
	 *                {@link String#format(String, Object...)} method
	 */
	public void debug(String message, Object... args) {
		if (!isDebugEnabled()) return;
		log(LogType.DEBUG, message, args);
	}
	
	/**
	 * Sends a debug message with a string supplier to the logger. The method does
	 * noting if logging is disabled. The string supplier will not be processed if
	 * the logging is disabled.
	 * 
	 * @param message supplier that builds the string output when it's called
	 */
	public void debug(Supplier<String> message) {
		if (!isDebugEnabled()) return;
		log(LogType.DEBUG, message);
	}
	
	/**
	 * Sends a message to the logger.
	 * 
	 * @param type    the type of the message
	 * @param message the message to send
	 */
	private void log(LogType type, String message) {
		final LocalDateTime timeStamp = LocalDateTime.now();
		final LoggingEvent loggingEvent = new LoggingEvent(this, formatter, type, timeStamp, Optional.ofNullable(prefix), message);
		final String output = formatter.apply(loggingEvent);
		if (loggingAction != null) loggingAction.accept(loggingEvent);
		if (systemOutput) System.out.println(output);
		if (fileOutput) {
			try {
				final Path outputFile = Path.of(fileDirectory.toString(), "log-" + timeStamp.format(DateTimeFormatter.ISO_LOCAL_DATE) + ".txt");
				if (!Files.exists(fileDirectory)) Files.createDirectories(fileDirectory);
				if (!Files.exists(outputFile)) Files.createFile(outputFile);
				Files.writeString(outputFile, output + "\n", StandardOpenOption.APPEND);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends a message to the logger.
	 * 
	 * @param type    the type of the message
	 * @param message the message to send
	 * @param args    arguments to insert in the message with the build in
	 *                {@link String#format(String, Object...)} method
	 */
	private void log(LogType type, String message, Object... args) {
		log(type, String.format(message, args));
	}
	
	/**
	 * Sends a message to the logger.
	 * 
	 * @param type   the type of the message
	 * @param string supplier that builds the string output when it's called
	 */
	private void log(LogType type, Supplier<String> string) {
		log(type, string.get());
	}
	
	/**
	 * Sets if the logging output should be printed to the standard output.
	 * 
	 * @param systemOutput if the logging output should be printed to the standard
	 *                     output
	 */
	public void setSystemOutput(boolean systemOutput) {
		this.systemOutput = systemOutput;
	}
	
	/**
	 * Returns if the logging output should be printed to the standard output.
	 * 
	 * @return if the logging output should be printed to the standard output
	 */
	public boolean isSystemOutput() {
		return systemOutput;
	}
	
	/**
	 * Sets if the logging output should be printed to a file. The file directory
	 * can be defined with {@link #setFileDirectory(File)}.
	 * 
	 * @param fileOutput if the logging output should be printed to a file
	 */
	public void setFileOutput(boolean fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	/**
	 * Returns if the logging output should be printed to a file. The file directory
	 * can be requested with {@link #getFileDirectory()}.
	 * 
	 * @return if the logging output should be printed to a file
	 */
	public boolean isFileOutput() {
		return fileOutput;
	}
	
	/**
	 * Enables or disables debug messages. Note that debug messages are even
	 * displayed if debug mode is disabled but the java virtual machine runs in
	 * debug mode.
	 * 
	 * @param debug if debug messages should be displayed
	 */
	public void setDebugEnabled(boolean debug) {
		this.debug = debug;
	}
	
	/**
	 * Returns if debug is enabled or java is currently running in debug mode. One
	 * of these has to be enabled to show debug messages.
	 * 
	 * @return if debug messages are enabled
	 */
	public boolean isDebugEnabled() {
		return debug || ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}
	
	/**
	 * Sets the directory in which the files for the logging output should be in.
	 * 
	 * @param fileDirectory the directory in which the files for the logging output
	 *                      should be in
	 */
	public void setFileDirectory(Path fileDirectory) {
		this.fileDirectory = fileDirectory;
	}
	
	/**
	 * Returns the directory in which the files for the logging output should be in.
	 * 
	 * @return the directory in which the files for the logging output should be in
	 */
	public Path getFileDirectory() {
		return fileDirectory;
	}
	
	/**
	 * Sets the format in which metadata should be in front of the logging
	 * messages.
	 * 
	 * @param formatter the format in which the metadata should be in front
	 *                          of the logging messages
	 */
	public void setFormatter(Function<LoggingEvent, String> formatter) {
		this.formatter = formatter;
	}
	
	/**
	 * Returns the format in which the metadata should be in front of the logging
	 * messages.
	 * 
	 * @return the format in which the metadata should be in front of the logging
	 *         messages
	 */
	public Function<LoggingEvent, String> getFormatter() {
		return formatter;
	}
	
	/**
	 * Sets an action to execute when something is logged.
	 * 
	 * @param outputAction an action to execute when something is logged
	 */
	public void setLoggingAction(Consumer<LoggingEvent> loggingAction) {
		this.loggingAction = loggingAction;
	}
	
	/**
	 * Returns an action to execute when something is logged.
	 * 
	 * @return an action to execute when something is logged
	 */
	public Consumer<LoggingEvent> getLoggingAction() {
		return loggingAction;
	}
	
}
