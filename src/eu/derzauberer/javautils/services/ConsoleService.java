package eu.derzauberer.javautils.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import eu.derzauberer.javautils.events.ConsoleInputEvent;
import eu.derzauberer.javautils.events.ConsoleOutputEvent;
import eu.derzauberer.javautils.util.Sender;

/**
 * A console that contains an input stream and output stream and simplifies the
 * usage of input and output functionalities. It also automatically removes
 * color codes on systems that do not support these.
 */
public class ConsoleService implements Sender {
	
	public static final String BLACK = "\u001b[30m";
	public static final String GRAY = "\u001b[30;1m";
	public static final String WHITE = "\u001b[37m";
	public static final String RED = "\u001b[31m";
	public static final String YELLOW = "\u001b[33m";
	public static final String GREEN = "\u001b[32m";
	public static final String CYAN = "\u001b[36m";
	public static final String BLUE = "\u001b[34m";
	public static final String MAGENTA = "\u001b[35m";
	
	public static final String BACKGROUND_BLACK = "\u001b[40m";
	public static final String BACKGROUND_GRAY = "\u001b[40;1m";
	public static final String BACKGROUND_WHITE = "\u001b[47m";
	public static final String BACKGROUND_RED = "\u001b[41m";
	public static final String BACKGROUND_YELLOW = "\u001b[43m";
	public static final String BACKGROUND_GREEN = "\u001b[42m";
	public static final String BACKGROUND_CYAN = "\u001b[46m";
	public static final String BACKGROUND_BLUE = "\u001b[44m";
	public static final String BACKGROUND_MAGENTA = "\u001b[45m";
	
	public static final String BOLD = "\u001b[1m";
	public static final String UNDERLINE = "\u001b[4m";
	public static final String REVERSED = "\u001b[7m";
	public static final String CROSSED_OUT = "\u001b[9m";
	
	public static final String RESET_COLOR = "\u001b[39m";
	public static final String RESET_BACKGROUND_COLOR = "\u001b[49m";
	public static final String RESET = "\u001b[0m";
	
	private final Thread thread;
	private final InputStream input;
	private final OutputStream output;
	private Charset charset;
	private CommandService commandHandler;
	private String prefix;
	private boolean hasColorCodesEnabled;
	private boolean isLoggingEnabled;
	private Path loggingDirectory;
	private boolean isClosed;
	private boolean nextLineIgnored;
	private Consumer<ConsoleInputEvent> inputAction;
	private Consumer<ConsoleOutputEvent> outputAction;
	
	/**
	 * Creates a new console with input reader and output printer.
	 */
	public ConsoleService() {
		this("", null);
	}
	
	/**
	 * Creates a new console with input reader and output printer.
	 * 
	 * @param prefix prefix he prefix which should be displayed in front of the
	 *               input line
	 */
	public ConsoleService(String prefix) {
		this(prefix, null);
	}

	/**
	 * Creates a new console with input reader and output printer.
	 * 
	 * @param commandController the command handler, in which the input from the
	 *                          console should be passed
	 */
	public ConsoleService(CommandService commandController) {
		this("", commandController);
	}

	/**
	 * Creates a new console with input reader and output printer.
	 * 
	 * @param prefix            prefix he prefix which should be displayed in front
	 *                          of the input line
	 * @param commandController the command handler, in which the input from the
	 *                          console should be passed
	 */
	public ConsoleService(String prefix, CommandService commandController) {
		input = System.in;
		output = System.out;
		charset = Charset.defaultCharset();
		this.prefix = prefix;
		this.commandHandler = commandController;
		hasColorCodesEnabled = hasSystemColorCodeSupport();
		isLoggingEnabled = false;
		loggingDirectory = Path.of("logs");
		isClosed = false;
		nextLineIgnored = false;
		thread = new Thread(this::inputLoop);
		thread.start();
	}
	
	/**
	 * Waits for incoming messages from the server socket.
	 */
	protected void inputLoop() {
		try {
			String input;
			while (!thread.isInterrupted() && !isClosed) {
				send(prefix);
				input = readLine();
				final ConsoleInputEvent event = new ConsoleInputEvent(this, input);
				if (inputAction != null && !event.isCancelled()) {
					inputAction.accept(event);
				}
				if (!event.isCancelled()) {
					if (commandHandler != null) commandHandler.executeCommand(event.getConsole(), event.getInput());
					if (isLoggingEnabled) log(prefix + " " + input);
				}
			}
		} catch (IOException exception) {}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void send(String string) {
		final ConsoleOutputEvent event = new ConsoleOutputEvent(this, string);
		if (outputAction != null && !event.isCancelled()) outputAction.accept(event);
		if (!event.isCancelled()) {
			if (hasColorCodesEnabled) Sender.super.send(event.getOutput());
			else Sender.super.send(removeColorCodes(event.getOutput()));
			if(isLoggingEnabled) log(removeColorCodes(event.getOutput()));
		}
	}
	
	/**
	 * Logs an input or output in the logging file.
	 * 
	 * @param string the input or output that should be written
	 */
	private void log(String string) {
		try {
			final Path loggingFile = Path.of(loggingDirectory.toString(), "log-" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".txt");
			if (!Files.exists(loggingDirectory)) Files.createDirectories(loggingDirectory);
			if (!Files.exists(loggingFile)) Files.createFile(loggingFile);
			Files.writeString(loggingFile, output + "\n", StandardOpenOption.APPEND);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	/**
	 * Removes all color codes from the string.
	 * 
	 * @param string the string to remove the color codes
	 * @return the string without color codes
	 */
	public static String removeColorCodes(String string) {
		return string.replaceAll("\u001B\\[[;\\d]*m", "");
	}
	
	/**
	 * Generates a 256-bit color code.
	 *  
	 * @param number the 256-bit color index
	 * @return the 256-bit color code
	 */
	public static String get256BitColorCode(int number) {
		return "\033[38;5;" + number + "m";
	}
	
	/**
	 * Generates a 256-bit background color code.
	 *  
	 * @param number the 256-bit color index
	 * @return the 256-bit background color code
	 */
	public static String get256BitBackgroundColorCode(int number) {
		return "\033[48;5;" + number + "m";
	}
	
	/**
	 * Returns the command handler, in which the input from the console should be
	 * passed to execute commands. The command controller can be null.
	 * 
	 * @param commandHandler the command handler, in which the input from the
	 *                       console should be passed
	 */
	public void setCommandHandler(CommandService commandHandler) {
		this.commandHandler = commandHandler;
	}
	
	/**
	 * Returns the command handler, in which the input from the console is passed to
	 * execute commands. The command controller can be null.
	 * 
	 * @return command handler, in which the input from the console is passed
	 */
	public CommandService getCommandHandler() {
		return commandHandler;
	}
	
	/**
	 * Sets the prefix which should be displayed in front of the input line.
	 * 
	 * @param prefix he prefix which should be displayed in front of the input line
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Returns the prefix which is displayed in front of the input line.
	 * 
	 * @return the prefix which is displayed in front of the input line
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Sets if the color codes should be enabled.
	 * 
	 * @param colorCodesEnabled if the color codes should be enabled
	 */
	public void setColorCodesEnabled(boolean colorCodesEnabled) {
		this.hasColorCodesEnabled = colorCodesEnabled;
	}
	
	/**
	 * Returns if the color codes are enabled.
	 * 
	 * @return if the color codes are enabled
	 */
	public boolean hasColorCodesEnabled() {
		return hasColorCodesEnabled;
	}
	
	/**
	 * Returns if the system supports color codes.
	 * 
	 * @return if the system supports color codes
	 */
	public boolean hasSystemColorCodeSupport() {
		return System.console() != null && !System.getProperty("os.name").toLowerCase().contains("windows") && System.getenv().get("TERM") != null;
	}
	
	/**
	 * Sets if the console input and output should be logged to a file.
	 *  
	 * @param logging if the console input and output should be logged
	 */
	public void setLoggingEnabled(boolean logging) {
		this.isLoggingEnabled = logging;
	}
	
	/**
	 * Returns if the console input and output is logged to a file.
	 * 
	 * @return if the console input and output is logged
	 */
	public boolean isLoggingEnabled() {
		return isLoggingEnabled;
	}
	
	/**
	 * Sets the directory in which the logging files should be written.
	 * 
	 * @param loggingDirectory the directory in which the logging files should be
	 *                         written
	 */
	public void setLoggingDirectory(Path loggingDirectory) {
		this.loggingDirectory = loggingDirectory;
	}
	
	/**
	 * Returns the directory in which the logging files are written.
	 * 
	 * @return the directory in which the logging files are written
	 */
	public Path getLoggingDirectory() {
		return loggingDirectory;
	}
	
	/**
	 * Closes the input reader of the console.
	 */
	public void close() {
		isClosed = false;
		thread.interrupt();
	}
	
	/**
	 * Returns if the input reader of the console is closed
	 * 
	 * @return if the input reader of the console is closed
	 */
	public boolean isClosed() {
		return isClosed;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getInputStream() {
		return input;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputStream getOutputStream() {
		return output;
	}
	
	/**
	 * Sets the charset for the console that is in use when converting
	 * bytes to strings.
	 * 
	 * @param charset the charset for the console
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Charset getCharset() {
		return charset;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNextLineIgnore(boolean nextLineIgnored) {
		this.nextLineIgnored = nextLineIgnored;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isNextLineIgnored() {
		return nextLineIgnored;
	}
	
	/**
	 * Sets an action to execute when an input was given to the console.
	 * 
	 * @param inputAction an action to execute when an input was given to the
	 *                    console
	 */
	public void setInputAction(Consumer<ConsoleInputEvent> inputAction) {
		this.inputAction = inputAction;
	}
	
	/**
	 * Returns an action to execute when an input was given to the console.
	 * 
	 * @return an action to execute when an input was given to the console
	 */
	public Consumer<ConsoleInputEvent> getInputAction() {
		return inputAction;
	}
	
	/**
	 * Sets an action to execute when an output was send from the console.
	 * 
	 * @param outputAction an action to execute when an output was send from the
	 *                    console
	 */
	public void setOutputAction(Consumer<ConsoleOutputEvent> outputAction) {
		this.outputAction = outputAction;
	}
	
	/**
	 * Returns an action to execute when an output was send from the console.
	 * 
	 * @return an action to execute when an output was send from the console
	 */
	public Consumer<ConsoleOutputEvent> getOutputAction() {
		return outputAction;
	}

}
