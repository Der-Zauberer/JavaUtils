package eu.derzauberer.javautils.util;

import java.io.File;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Pattern;
import eu.derzauberer.javautils.action.ConsoleOutputAction;
import eu.derzauberer.javautils.events.ConsoleInputEvent;
import eu.derzauberer.javautils.events.ConsoleOutputEvent;
import eu.derzauberer.javautils.handler.CommandHandler;
import eu.derzauberer.javautils.handler.EventHandler;
import eu.derzauberer.javautils.handler.FileHandler;

public class Console implements Runnable {

	public enum MessageType {DEFAULT, INFO, SUCCESS, WARNING, ERROR}

	private Thread thread;
	private String prefix;
	private File directory;
	private ConsoleOutputAction outputAction;
	private Object sender;
	private boolean logEnabled;
	private boolean logTimestampEnabled;
	private File logDirectory;
	private File latestLogFile;
	
	public Console() {
		this(true);
	}
	
	public Console(boolean start) {
		if (start) startConsole();
		directory = FileHandler.getJarDirectory();
		prefix = "";
		outputAction = output -> System.out.println(output);
		sender = System.in;
		logEnabled = false;
		logDirectory = new File(FileHandler.getJarDirectory(), "logs");
	}
	
	public Console(String prefix) {
		this(prefix, true);
	}
	
	public Console(String prefix, boolean start) {
		if (start) startConsole();
		directory = FileHandler.getJarDirectory();
		this.prefix = prefix;
		outputAction = output -> System.out.println(output);
	}
	
	public void startConsole() {
		thread = new Thread(this);
		thread.start();
	}

	public void stopConsole() {
		thread.interrupt();
	}
	
	@SuppressWarnings("resource")
	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		String input;
		while (!thread.isInterrupted()) {
			System.out.print(prefix);
			input = scanner.nextLine();
			sendInput(input);
		}
	}

	public  void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}
	
	public File getDirectory() {
		return directory;
	}
	
	public void setOutputAction(ConsoleOutputAction outputAction) {
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
		EventHandler.executeEvent(ConsoleInputEvent.class, event);
		if (!event.isCancled()) {
			CommandHandler.executeCommand(event.getConsole(), event.getInput());
		}
		log(event.getInput());
	}
	
	private void sendOutput(String output, MessageType type) {
		ConsoleOutputEvent event = new ConsoleOutputEvent(this, output, type);
		EventHandler.executeEvent(ConsoleOutputEvent.class, event);
		if (event.getMessageType() != MessageType.DEFAULT) {
			event.setOutput("[" + type.toString() + "] " + event.getOutput());
		}
		if (!event.isCancled()) {
			if (logTimestampEnabled) {
				outputAction.onAction(getTimeStamp() + event.getOutput());
			} else {
				outputAction.onAction(event.getOutput());
			}
		}
		log(event.getOutput());
	}
	
	public void sendMessage(Object object) {
		sendOutput(object.toString(), MessageType.DEFAULT);
	}

	public void sendMessage(Object object, MessageType type) {
		sendOutput(object.toString(), type);
	}

	public void sendMessage(String string, String... args) {
		for (int i = 0; i < args.length && string.contains("{}"); i++) {
			string = string.replaceFirst(Pattern.quote("{}"), args[i]);
		}
		sendOutput(string, MessageType.DEFAULT);
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