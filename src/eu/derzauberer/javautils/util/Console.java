package eu.derzauberer.javautils.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;
import eu.derzauberer.javautils.action.ConsoleInputAction;
import eu.derzauberer.javautils.action.ConsoleOutputAction;
import eu.derzauberer.javautils.events.ConsoleInputEvent;
import eu.derzauberer.javautils.events.ConsoleOutputEvent;
import eu.derzauberer.javautils.handler.CommandHandler;
import eu.derzauberer.javautils.handler.FileHandler;

public class Console implements Runnable {
	
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
	private String inputPrefix;
	private File directory;
	private MessageType defaultType;
	private boolean debugEnabled;
	private ConsoleInputAction inputAction;
	private ConsoleOutputAction outputAction;
	private Object sender;
	private boolean ansiEscapeCodesEnabled;
	private boolean logEnabled;
	private boolean logTimestampEnabled;
	private File logDirectory;
	private File latestLogFile;
	
	public Console() {
		this(true); 
	}
	
	public Console(boolean start) {
		this("", start);
	}
	
	public Console(String inputPrefix) {
		this(inputPrefix, true);
	}
	
	public Console(String inputPrefix, boolean start) {
		if (start) start();
		directory = FileHandler.getJarDirectory();
		this.inputPrefix = inputPrefix;
		defaultType = MessageType.DEFAULT;
		debugEnabled = false;
		inputAction = event -> {};
		outputAction = event -> System.out.println(event.getOutput());
		sender = System.in;
		ansiEscapeCodesEnabled = false;
		logEnabled = false;
		logDirectory = new File(FileHandler.getJarDirectory(), "logs");
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		thread.interrupt();
	}
	
	@SuppressWarnings("resource")
	@Override
	public void run() {
		try {
			Scanner scanner = new Scanner(System.in);
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
			CommandHandler.executeCommand(event.getConsole(), event.getInput());
			log(event.getInput());
		}
	}
	
	private void sendOutput(String output, MessageType type) {
		if (type != MessageType.DEBUG || debugEnabled) {
			ConsoleOutputEvent event = new ConsoleOutputEvent(this, output, type);
			if (event.getMessageType() != MessageType.DEFAULT) {
				event.setOutput("[" + type.toString() + "] " + event.getOutput());
			}
			if (!ansiEscapeCodesSupportetBySystem()) {
				event.setOutput(removeEscapeCodes(event.getOutput()));
			}
			if (logTimestampEnabled) {
				event.setOutput(getTimeStamp() + event.getOutput());
			}
			if (!event.isCancelled()) {
				outputAction.onAction(event);
				log(removeEscapeCodes(event.getOutput()));
			}
		}
	}
	
	public static String removeEscapeCodes(String string) {
		try {
			for (Field field : Console.class.getFields()) {
				string = string.replace(field.get(Console.class).toString(), "");
			}
		} catch (IllegalArgumentException exception) {
		} catch (IllegalAccessException exception) {
		}
		while (string.contains("\033[38;5;")) {
			int index = 0;
			for (int i = string.indexOf("\033[38;5;"); i < string.length(); i++) {
				if (string.charAt(i) == 'm') {
					index = i;
					break;
				}
			}
			string = string.substring(0, string.indexOf("\\") + 5) + string.substring(index + 1);
		}
		return string;
	}
	
	public static String get256BitColor(int number) {
		return "\033[38;5;" + number + "m";
	}
	
	public static String get256BitBackgroundColor(int number) {
		return "\033[48;5;" + number + "m";
	}
	
	public String nextInput() {
		String input = "";
		Scanner scanner = new Scanner(System.in);
		input = scanner.next();
		scanner.close();
		return input;
	}
	
	public void sendMessage(Object object) {
		sendOutput(object.toString(), defaultType);
	}

	public void sendMessage(Object object, MessageType type) {
		sendOutput(object.toString(), type);
	}

	public void sendMessage(String string, String... args) {
		for (int i = 0; i < args.length && string.contains("{}"); i++) {
			string = string.replaceFirst(Pattern.quote("{}"), args[i]);
		}
		sendOutput(string, defaultType);
	}

	public void sendMessage(String string, MessageType type, String... args) {
		for (int i = 0; i < args.length && string.contains("{}"); i++) {
			string = string.replaceFirst(Pattern.quote("{}"), args[i]);
		}
		sendOutput(string, type);
	}
	
	private void log(String string) {
		if (isLogEnabled() && logDirectory != null) {
			if (logTimestampEnabled) {
				string = getTimeStamp() + string;
			}
			String name = "log-" + getDate() + ".txt";
			if (latestLogFile == null || !latestLogFile.exists() || !latestLogFile.getName().equals(name)) {
				latestLogFile = new File(logDirectory, name);
			}
			if (!string.endsWith("\n")) {
				string += "\n";
			}
			FileHandler.appendString(latestLogFile, string);
		}
	}
	
	private String getTimeStamp() {
		Calendar calendar = Calendar.getInstance();
		String string[] = new String[3];
		string[0] = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		string[1] = Integer.toString(calendar.get(Calendar.MINUTE));
		string[2] = Integer.toString(calendar.get(Calendar.SECOND));
		for (int i = 0; i < string.length; i++) {
			if (string[i].length() == 1) {
				string[i] = "0" + string[i];
			}
		}
		return "[" + string[0] + ":" + string[1] + ":" + string[2] + "] ";
	}
	
	private String getDate() {
		Calendar calendar = Calendar.getInstance();
		String string[] = new String[3];
		string[0] = Integer.toString(calendar.get(Calendar.YEAR));
		string[1] = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		string[2] = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		for (int i = 1; i < string.length; i++) {
			if (string[i].length() == 1) {
				string[i] = "0" + string[i];
			}
		}
		return string[0] + "-" + string[1] + "-" + string[2];
	}
	
	public boolean isAnsiEscapeCodesEnabled() {
		return ansiEscapeCodesEnabled;
	}
	
	public boolean ansiEscapeCodesSupportetBySystem() {
		return ansiEscapeCodesEnabled || (System.console() != null && !System.getProperty("os.name").toLowerCase().contains("windows") && System.getenv().get("TERM") != null);
	}
	
	public void setAnsiEscapeCodesEnabled(boolean ansiEscapeCodesEnabled) {
		this.ansiEscapeCodesEnabled = ansiEscapeCodesEnabled;
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