package eu.derzauberer.javautils.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Scanner;

import eu.derzauberer.javautils.action.ConsoleInputAction;
import eu.derzauberer.javautils.action.ConsoleOutputAction;
import eu.derzauberer.javautils.events.ConsoleInputEvent;
import eu.derzauberer.javautils.events.ConsoleOutputEvent;
import eu.derzauberer.javautils.handler.CommandHandler;

public class Console implements Sender {
	
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

	private MessageType defaultMessageType;
	private CommandHandler commandHandler;
	private boolean isRunning;
	private boolean areColorCodesEnabled;
	private boolean isTimestampEnabled;
	private boolean isLogEnabled;
	private ConsoleInputAction inputAction;
	private ConsoleOutputAction outputAction;
	private String inputPrefix;
	private Thread thread;
	private Scanner scanner;
	private File directory;
	private File logDirectory;
	private File latestLogFile;
	
	public Console() {
		this(true); 
	}
	
	public Console(boolean start) {
		this("", start);
	}
	
	public Console(CommandHandler handler) {
		this("", handler);
	}
	
	public Console(String inputPrefix) {
		this(inputPrefix, true);
	}
	
	public Console(String inputPrefix, CommandHandler commandHandler) {
		this(inputPrefix, true, commandHandler);
	}
	
	public Console(String inputPrefix, boolean start) {
		this(inputPrefix, start, null);
	}
	
	public Console(String inputPrefix, boolean start, CommandHandler commandHandler) {
		defaultMessageType = MessageType.DEFAULT;
		this.commandHandler = commandHandler;
		isRunning = false;
		areColorCodesEnabled = areColorCodesSupportedBySystem();
		isTimestampEnabled = false;
		isLogEnabled = false;
		this.inputPrefix = inputPrefix;
		if (start) start();
		scanner = new Scanner(System.in);
		directory = FileUtil.getJarDirectory();
		logDirectory = new File(FileUtil.getJarDirectory(), "logs");
	}
	
	public void start() {
		if (!isRunning) {
			isRunning = true;
			thread = new Thread(this::inputLoop);
			thread.start();
		}
		
	}

	public void stop() {
		if (isRunning) {
			isRunning = false;
			thread.interrupt();
		}
	}
	
	private void inputLoop() {
		try {
			scanner = new Scanner(System.in);
			String input;
			while (!thread.isInterrupted()) {
				System.out.print(inputPrefix);
				input = scanner.nextLine();
				sendInput(input);
			}
		} catch (NoSuchElementException exception) {}
	}
	
	@Override
	public void sendInput(String string) {
		ConsoleInputEvent event = new ConsoleInputEvent(this, string);
		if (inputAction != null && !event.isCancelled()) inputAction.onAction(event);
		if (!event.isCancelled()) {
			if (commandHandler != null) commandHandler.executeCommand(event.getConsole(), event.getInput());
			if (isLogEnabled) log(inputPrefix + " " + string);
		}
	}
	
	@Override
	public void sendOutput(String string, MessageType type) {
		ConsoleOutputEvent event = new ConsoleOutputEvent(this, string, type);
		if (outputAction != null && !event.isCancelled()) outputAction.onAction(event);
		if (!event.isCancelled()) {
			if (!areColorCodesEnabled() && !areColorCodesSupportedBySystem()) event.setOutput(removeEscapeCodes(event.getOutput()));
			if (isTimestampEnabled) event.setOutput(Time.now().toString("[hh:mm:ss] ") + event.getOutput());
			System.out.println(event.getOutput());
			if (isLogEnabled) {
				log(removeEscapeCodes(event.getOutput()));
			}
		}
	}
	
	public static String removeEscapeCodes(String string) {
		String output = string;
		try {
			for (Field field : Console.class.getFields()) {
				output = output.replace(field.get(Console.class).toString(), "");
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
		if (isLogEnabled() && logDirectory != null) {
			String name = "log-" + Date.now() + ".txt";
			if (latestLogFile == null || !latestLogFile.exists() || !latestLogFile.getName().equals(name)) {
				latestLogFile = new File(logDirectory, name);
			}
			FileUtil.appendString(latestLogFile, (string.endsWith("\n")) ? string : string + "\n");
		}
	}
	
	public void setDefaultMessageType(MessageType defaultType) {
		this.defaultMessageType = defaultType;
	}
	
	@Override
	public MessageType getDefaultMessageType() {
		return defaultMessageType;
	}
	
	public void setCommandHandler(CommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}
	
	public CommandHandler getCommandHandler() {
		return commandHandler;
	}
	
	public boolean isRunning() {
		return isRunning;
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
	
	public void setTimestampEnabled(boolean logTimestampEnabled) {
		this.isTimestampEnabled = logTimestampEnabled;
	}
	
	public boolean isTimestampEnabled() {
		return isTimestampEnabled;
	}
	
	public void setLogEnabled(boolean logEnabled) {
		this.isLogEnabled = logEnabled;
	}
	
	public boolean isLogEnabled() {
		return isLogEnabled;
	}
	
	public void setOnInput(ConsoleInputAction inputAction) {
		this.inputAction = inputAction;
	}
	
	public ConsoleInputAction getOnInput() {
		return inputAction;
	}
	
	public void setOnOutput(ConsoleOutputAction outputAction) {
		this.outputAction = outputAction;
	}
	
	public ConsoleOutputAction getOnOutput() {
		return outputAction;
	}

	public  void setInputPrefix(String inputPrefix) {
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
	
	public void setLogDirectory(File logDirectory) {
		this.logDirectory = logDirectory;
	}
	
	public File getLogDirectory() {
		return logDirectory;
	}

}