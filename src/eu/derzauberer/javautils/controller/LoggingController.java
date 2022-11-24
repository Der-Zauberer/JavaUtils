package eu.derzauberer.javautils.controller;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import eu.derzauberer.javautils.events.LoggingEvent;
import eu.derzauberer.javautils.util.FileUtil;

/**
 * The logger collects messages from the {@link #log(String)} methods and send
 * the output with timestamp, type and message to the console and writes it in a
 * logging file if enabled. You can enable the options with
 * {@link #setSystemOutput(boolean)} and {@link #setFileOutput(boolean)}. The
 * use of one of the {@link #log(String)} functions triggers a
 * {@link LoggingEvent}.
 */
public class LoggingController {
	
	/**
	 * Represents the type of logging outputs.
	 */
	public enum LogType {
		INFO,
		WARN,
		ERROR,
		DEBUG
	}
	
	private final String prefix;
	
	private static boolean systemOutput = true;
	private static boolean fileOutput = true;
	private static File fileDirectory = new File("logs");
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	/**
	 *  Creates a new logging controller without a prefix.
	 */
	public LoggingController() {
		this.prefix = "";
	}
	
	/**
	 * Creates a new logging controller with a prefix. The prefix will be displayed
	 * between the logging type and the message.
	 * 
	 * @param prefix the prefix that will be displayed between the logging type and
	 *               the message
	 */
	public LoggingController(String  prefix) {
		this.prefix = prefix + ": ";
	}
	
	/**
	 * Creates a new logging controller with the name of the class as prefix. The prefix will be displayed
	 * between the logging type and the message.
	 * 
	 * @param classType the class of which the name will be displayed as prefix between the logging type and
	 *               the message
	 */
	public LoggingController(Class<?> classType) {
		prefix = classType.getName() + ": ";
	}
	
	/**
	 * Sends an information message to the logger. The output will not be displayed
	 * if the type is {@link LogType#DEBUG} and the java virtual machine does nut run in
	 * debug mode. The default type, if no one is given, is {@link LogType#INFO}.
	 * 
	 * @param message the message to send
	 */
	public void log(String message) {
		log(LogType.INFO, message);
	}
	
	/**
	 * Sends an information message to the logger. The output will not be displayed
	 * if the type is {@link LogType#DEBUG} and the java virtual machine does nut
	 * run in debug mode. The default type, if no one is given, is
	 * {@link LogType#INFO}.
	 * 
	 * @param message the message to send
	 * @param args    arguments to insert in the message with the build in
	 *                {@link String#format(String, Object...)} method
	 */
	public void log(String message, Object... args) {
		log(LogType.INFO, String.format(message, args));
	}
	
	/**
	 * Sends a message to the logger. The output will not be displayed
	 * if the type is {@link LogType#DEBUG} and the java virtual machine does nut
	 * run in debug mode.
	 * 
	 * @param type    the type of the message
	 * @param message the message to send
	 */
	public void log(LogType type, String message) {
		if (type == LogType.DEBUG && !isDebugEnabled()) return;
		final LocalDateTime timeStapm = LocalDateTime.now();
		final String output = "[" + timeStapm.format(dateTimeFormatter) + " " + type + "] " + prefix + message;
		final LoggingEvent event = new LoggingEvent(this, type, message, timeStapm, output);
		EventController.getGlobalEventController().callListeners(event);
		if (systemOutput) System.out.println(output);
		if (fileOutput) {
			try {
				FileUtil.appendString(new File(fileDirectory, "log-" + timeStapm.format(DateTimeFormatter.ISO_LOCAL_DATE) + ".txt"), output + "\n");
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends a message to the logger. The output will not be displayed
	 * if the type is {@link LogType#DEBUG} and the java virtual machine does nut
	 * run in debug mode.
	 * 
	 * @param type   the type of the message
	 * @param message the message to send
	 * @param args   arguments to insert in the message with the build in
	 *               {@link String#format(String, Object...)} method
	 */
	public void log(LogType type, String message, Object... args) {
		log(type, String.format(message, args));
	}
	
	/**
	 * Returns if java is currently running in debug mode.
	 * 
	 * @return if java is currently running in debug mode
	 */
	public boolean isDebugEnabled() {
		return ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}
	
	/**
	 * Sets if the logging output should be printed to the standard output.
	 * 
	 * @param systemOutput if the logging output should be printed to the standard
	 *                     output
	 */
	public static void setSystemOutput(boolean systemOutput) {
		LoggingController.systemOutput = systemOutput;
	}
	
	/**
	 * Returns if the logging output should be printed to the standard output.
	 * 
	 * return if the logging output should be printed to the standard output
	 */
	public static boolean isSystemOutput() {
		return systemOutput;
	}
	
	/**
	 * Sets if the logging output should be printed to a file. The file directory
	 * can be defined with {@link #setFileDirectory(File)}.
	 * 
	 * @param fileOutput if the logging output should be printed to a file
	 */
	public static void setFileOutput(boolean fileOutput) {
		LoggingController.fileOutput = fileOutput;
	}
	
	/**
	 * Returns if the logging output should be printed to a file. The file directory
	 * can be requested with {@link #getFileDirectory()}.
	 * 
	 * return if the logging output should be printed to a file
	 */
	public static boolean isFileOutput() {
		return fileOutput;
	}
	
	/**
	 * Sets the directory in which the files for the logging output should be in.
	 * 
	 * @param fileDirectory the directory in which the files for the logging output
	 *                      should be in
	 */
	public static void setFileDirectory(File fileDirectory) {
		LoggingController.fileDirectory = fileDirectory;
	}
	
	/**
	 * Returns the directory in which the files for the logging output should be in.
	 * 
	 * @return the directory in which the files for the logging output should be in
	 */
	public static File getFileDirectory() {
		return fileDirectory;
	}
	
	/**
	 * Sets the format in which the timestamp should be in front of the logging
	 * messages.
	 * 
	 * @param dateTimeFormatter the format in which the timestamp should be in front
	 *                          of the logging messages
	 */
	public static void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
		LoggingController.dateTimeFormatter = dateTimeFormatter;
	}
	
	/**
	 * Returns the format in which the timestamp should be in front of the logging
	 * messages.
	 * 
	 * @return the format in which the timestamp should be in front of the logging
	 *         messages
	 */
	public static DateTimeFormatter getDateTimeFormatter() {
		return dateTimeFormatter;
	}
	
}