package eu.derzauberer.javautils.controller;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Consumer;
import eu.derzauberer.javautils.events.ConsoleInputEvent;
import eu.derzauberer.javautils.events.ConsoleOutputEvent;
import eu.derzauberer.javautils.util.FileUtil;
import eu.derzauberer.javautils.util.Sender;

public class ConsoleController implements Sender, Closeable {
	
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
	private final Scanner scanner;
	private CommandController commandHandler;
	private boolean areColorCodesEnabled;
	private String inputPrefix;
	private File directory;
	private boolean isLogEnabled;
	private File logDirectory;
	private boolean closed;
	private Consumer<ConsoleInputEvent> inputAction;
	private Consumer<ConsoleOutputEvent> outputAction;
	
	public ConsoleController() {
		this(true); 
	}
	
	public ConsoleController(boolean start) {
		this("", start);
	}
	
	public ConsoleController(CommandController handler) {
		this("", handler);
	}
	
	public ConsoleController(String inputPrefix) {
		this(inputPrefix, true);
	}
	
	public ConsoleController(String inputPrefix, CommandController commandHandler) {
		this(inputPrefix, true, commandHandler);
	}
	
	public ConsoleController(String inputPrefix, boolean start) {
		this(inputPrefix, start, null);
	}
	
	public ConsoleController(String inputPrefix, boolean start, CommandController commandHandler) {
		thread = new Thread(this::inputLoop);
		scanner = new Scanner(System.in);
		this.commandHandler = commandHandler;
		areColorCodesEnabled = areColorCodesSupportedBySystem();
		isLogEnabled = false;
		this.inputPrefix = inputPrefix;
		directory = FileUtil.getExecutionDirectory();
		logDirectory = new File("logs");
		closed = false;
	}
	
	protected void inputLoop() {
		try {
			String input;
			while (!thread.isInterrupted()) {
				System.out.print(inputPrefix);
				input = scanner.nextLine();
				final ConsoleInputEvent event = new ConsoleInputEvent(this, input);
				EventController.getGlobalEventController().callListeners(event);
				if (inputAction != null && !event.isCancelled()) {
					inputAction.accept(event);
				}
				if (!event.isCancelled()) {
					if (commandHandler != null) commandHandler.executeCommand(event.getConsole(), event.getInput());
					if (isLogEnabled) log(inputPrefix + " " + input);
				}
			}
		} catch (NoSuchElementException exception) {}
	}
	
	@Override
	public byte[] readBytes(int lenght) throws IOException {
		return System.in.readNBytes(lenght);
	}

	@Override
	public int readBytes(byte[] bytes) throws IOException {
		return System.in.read(bytes);
	}

	@Override
	public String readLine() throws IOException {
		return scanner.nextLine();
	}
	
	@Override
	public void sendBytes(byte[] bytes) {
		System.out.print(new String(bytes, getCharset()));
	}
	
	@Override
	public void sendLine(String string) {
		final ConsoleOutputEvent event = new ConsoleOutputEvent(this, string);
		EventController.getGlobalEventController().callListeners(event);
		if (outputAction != null && !event.isCancelled()) outputAction.accept(event);
		if (!event.isCancelled()) {
			if (!areColorCodesEnabled() && !areColorCodesSupportedBySystem()) event.setOutput(removeEscapeCodes(event.getOutput()));
			System.out.println(event.getOutput());
			if (isLogEnabled) {
				log(removeEscapeCodes(event.getOutput()));
			}
		}
		Sender.super.sendLine(string);
	}
	
	public static String removeEscapeCodes(String string) {
		String output = string;
		try {
			for (Field field : ConsoleController.class.getFields()) {
				output = output.replace(field.get(ConsoleController.class).toString(), "");
			}
		} catch (IllegalArgumentException | IllegalAccessException exception) {}
		while (output.contains("\033[38;5;")) {
			int index = 0;
			for (int i = output.indexOf("\033[38;5;"); i < output.length(); i++) {
				if (output.charAt(i) == 'm') {
					index = i;
					break;
				}
			}
			output = output.substring(0, output.indexOf("\\") + 5) + output.substring(index + 1);
		}
		return output;
	}
	
	public static String get256BitColor(int number) {
		return "\033[38;5;" + number + "m";
	}
	
	public static String get256BitBackgroundColor(int number) {
		return "\033[48;5;" + number + "m";
	}
	
	private void log(String string) {
		try {
			FileUtil.appendString(new File(logDirectory, "log-" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".txt"), string + "\n");
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	public void setCommandHandler(CommandController commandHandler) {
		this.commandHandler = commandHandler;
	}
	
	public CommandController getCommandHandler() {
		return commandHandler;
	}
	
	
	public void setColorCodesEnabled(boolean colorCodesEnabled) {
		this.areColorCodesEnabled = colorCodesEnabled;
	}
	
	public boolean areColorCodesEnabled() {
		return areColorCodesEnabled;
	}
	
	public boolean areColorCodesSupportedBySystem() {
		return areColorCodesEnabled || (System.console() != null && !System.getProperty("os.name").toLowerCase().contains("windows") && System.getenv().get("TERM") != null);
	}
	
	public void setInputPrefix(String inputPrefix) {
		this.inputPrefix = inputPrefix;
	}

	public String getInputPrefix() {
		return inputPrefix;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}
	
	public File getDirectory() {
		return directory;
	}
	
	public void setLogEnabled(boolean logEnabled) {
		this.isLogEnabled = logEnabled;
	}
	
	public boolean isLogEnabled() {
		return isLogEnabled;
	}
	
	public void setLogDirectory(File logDirectory) {
		this.logDirectory = logDirectory;
	}
	
	public File getLogDirectory() {
		return logDirectory;
	}
	
	@Override
	public void close() throws IOException {
		thread.interrupt();
		scanner.close();
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public void setInputAction(Consumer<ConsoleInputEvent> inputAction) {
		this.inputAction = inputAction;
	}
	
	public Consumer<ConsoleInputEvent> getInputAction() {
		return inputAction;
	}
	
	public void setOutputAction(Consumer<ConsoleOutputEvent> outputAction) {
		this.outputAction = outputAction;
	}
	
	public Consumer<ConsoleOutputEvent> getOutputAction() {
		return outputAction;
	}

	@Override
	public Charset getCharset() {
		return Charset.defaultCharset();
	}

}