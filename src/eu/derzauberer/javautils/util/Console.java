package eu.derzauberer.javautils.util;

import java.io.File;
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
	
	public Console() {
		this(true);
	}
	
	public Console(boolean start) {
		if (start) startConsole();
		directory = FileHandler.getJarDirectory();
		prefix = "";
		outputAction = output -> System.out.println(output);
		sender = System.in;
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
		if(!event.isCancled()) {
			CommandHandler.executeCommand(event.getConsole(), event.getInput());
		}
	}
	
	private void sendOutput(String output, MessageType type) {
		ConsoleOutputEvent event = new ConsoleOutputEvent(this, output, type);
		EventHandler.executeEvent(ConsoleOutputEvent.class, event);
		if (event.getMessageType() != MessageType.DEFAULT) {
			event.setOutput("[" + type.toString() + "] " + event.getOutput());
		}
		if(!event.isCancled()) {
			outputAction.onAction(event.getOutput());
		}
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

}