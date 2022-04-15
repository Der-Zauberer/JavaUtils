package eu.derzauberer.javautils.handler;

import java.io.File;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;
import eu.derzauberer.javautils.action.ConsoleInputAction;
import eu.derzauberer.javautils.action.ConsoleOutputAction;
import eu.derzauberer.javautils.events.ConsoleInputEvent;
import eu.derzauberer.javautils.events.ConsoleOutputEvent;
import eu.derzauberer.javautils.util.Date;
import eu.derzauberer.javautils.util.FileUtil;
import eu.derzauberer.javautils.util.Time;

public class ConsoleHandler implements Runnable {
	
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

	public enum MessageType {DEFAULT, INFO, SUCCESS, WARNING, ERROR, DEBUG}

	private Thread thread;
	private Scanner scanner;
	private String inputPrefix;
	private File directory;
	private MessageType defaultType;
	private boolean debugEnabled;
	private ConsoleInputAction inputAction;
	private ConsoleOutputAction outputAction;
	private CommandHandler commandHandler;
	private Object sender;
	private boolean colorCodesEnabled;
	private boolean logEnabled;
	private boolean logTimestampEnabled;
	private File logDirectory;
	private File latestLogFile;
	
	public ConsoleHandler() {
		this(true); 
	}
	
	public ConsoleHandler(boolean start) {
		this("", start);
	}
	
	public ConsoleHandler(CommandHandler handler) {
		this("", handler);
	}
	
	public ConsoleHandler(String inputPrefix) {
		this(inputPrefix, true);
	}
	
	public ConsoleHandler(String inputPrefix, CommandHandler commandHandler) {
		this(inputPrefix, true, commandHandler);
	}
	
	public ConsoleHandler(String inputPrefix, boolean start) {
		this(inputPrefix, start, null);
	}
	
	public ConsoleHandler(String inputPrefix, boolean start, CommandHandler commandHandler) {
		this.inputPrefix = inputPrefix;
		this.commandHandler = commandHandler;
		if (start) start();
		scanner = new Scanner(System.in);
		directory = FileUtil.getJarDirectory();
		defaultType = MessageType.DEFAULT;
		debugEnabled = false;
		inputAction = event -> {};
		outputAction = event -> System.out.println(event.getOutput());
		sender = System.in;
		colorCodesEnabled = colorCodesSupportedBySystem();
		logEnabled = false;
		logDirectory = new File(FileUtil.getJarDirectory(), "logs");
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		thread.interrupt();
	}
	
	@Override
	public void run() {
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
	
	public void setDefaultType(MessageType defaultType) {
		this.defaultType = defaultType;
	}
	
	public MessageType getDefaultType() {
		return defaultType;
	}
	
	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}
	
	public boolean isDebugEnabled() {
		return debugEnabled;
	}
	
	public void setOnInput(ConsoleInputAction inputAction) {
		this.inputAction = inputAction;
	}
	
	public void setOnOutput(ConsoleOutputAction outputAction) {
		this.outputAction = outputAction;
	}
	
	public void setCommandHandler(CommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}
	
	public CommandHandler getCommandHandler() {
		return commandHandler;
	}
	
	public void setSender(Object sender) {
		this.sender = sender;
	}
	
	public Object getSender() {
		return sender;
	}
	
	public void sendInput(String string) {
		ConsoleInputEvent event = new ConsoleInputEvent(this, string);
		if (!event.isCancelled()) {
			inputAction.onAction(event);
			if (commandHandler != null) commandHandler.executeCommand(event.getConsole(), event.getInput());
			log(event.getInput());
		}
	}
	
	private void sendOutput(String output, MessageType type) {
		if (type != MessageType.DEBUG || debugEnabled) {
			ConsoleOutputEvent event = new ConsoleOutputEvent(this, output, type);
			if (!event.isCancelled()) {
				if (event.getMessageType() != MessageType.DEFAULT) {
					event.setOutput("[" + type.toString() + "] " + event.getOutput());
					event.setOutput(event.getOutput().replace("\n", "\n" + "[" + type.toString() + "] "));
				}
				if (!colorCodesSupportedBySystem()) event.setOutput(removeEscapeCodes(event.getOutput()));
				if (logTimestampEnabled) {
					event.setOutput(Time.now().toString("[hh:mm:ss] ") + event.getOutput());
					event.setOutput(event.getOutput().replace("\n", "\n" + Time.now().toString("[hh:mm:ss] ")));
				}
				outputAction.onAction(event);
				log(removeEscapeCodes(event.getOutput()));
			}
		}
	}
	
	public static String removeEscapeCodes(String string) {
		String output = string;
		try {
			for (Field field : ConsoleHandler.class.getFields()) {
				output = output.replace(field.get(ConsoleHandler.class).toString(), "");
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
	
	public void sendMessage(Object object) {
		sendOutput(object.toString(), defaultType);
	}

	public void sendMessage(Object object, MessageType type) {
		sendOutput(object.toString(), type);
	}

	public void sendMessage(String string, String... args) {
		String output = string;
		for (int i = 0; i < args.length && output.contains("{}"); i++) {
			output = output.replaceFirst(Pattern.quote("{}"), args[i]);
		}
		sendOutput(output, defaultType);
	}

	public void sendMessage(String string, MessageType type, String... args) {
		String output = string;
		for (int i = 0; i < args.length && output.contains("{}"); i++) {
			output = output.replaceFirst(Pattern.quote("{}"), args[i]);
		}
		sendOutput(output, type);
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
	
	public boolean areColorCodesEnabled() {
		return colorCodesEnabled;
	}
	
	public boolean colorCodesSupportedBySystem() {
		return colorCodesEnabled || (System.console() != null && !System.getProperty("os.name").toLowerCase().contains("windows") && System.getenv().get("TERM") != null);
	}
	
	public void setColorCodesEnabled(boolean colorCodesEnabled) {
		this.colorCodesEnabled = colorCodesEnabled;
	}
	
	public void setLogEnabled(boolean logEnabled) {
		this.logEnabled = logEnabled;
	}
	
	public boolean isLogEnabled() {
		return logEnabled;
	}
	
	public void setLogTimestampEnabled(boolean logTimestampEnabled) {
		this.logTimestampEnabled = logTimestampEnabled;
	}
	
	public boolean isLogTimestampEnabled() {
		return logTimestampEnabled;
	}
	
	public void setLogDirectory(File logDirectory) {
		this.logDirectory = logDirectory;
	}
	
	public File getLogDirectory() {
		return logDirectory;
	}

}