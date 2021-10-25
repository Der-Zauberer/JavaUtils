package javautils.util;

import java.io.File;
import java.util.Scanner;
import javauitls.events.ConsoleInputEvent;
import javauitls.events.ConsoleOutputEvent;
import javautils.handler.CommandHandler;
import javautils.handler.EventHandler;
import javautils.handler.FileHandler;

public class Console implements Runnable {

	private Thread thread;
	private String prefix;
	private File directory;

	public Console() {
		startConsole();
		directory = FileHandler.getJarDirectory();
	}
	
	public Console(String prefix) {
		startConsole(prefix);
		directory = FileHandler.getJarDirectory();
	}
	
	public void startConsole() {
		startConsole("");
	}
	
	public void startConsole(String prefix) {
		this.prefix = prefix;
		thread = new Thread(this);
		thread.start();
		Logger.addConsole(this);
	}

	public void stopConsole() {
		Logger.removeConsole(this);
		thread.interrupt();
	}
	
	@SuppressWarnings("resource")
	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		String input;
		while (!thread.isInterrupted()) {
			System.out.print(prefix + "~ ");
			input = scanner.nextLine();
			ConsoleInputEvent event = new ConsoleInputEvent(this, input);
			EventHandler.executeEvent(EventType.CONSOLEINPUTEVENT, event);
			if(!event.isCancled()) {
				CommandHandler.executeCommand(event.getConsole(), event.getInput());
			}
		}
	}

	public  void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}
	
	public File getDirectory() {
		return directory;
	}
	
	public void setDirectory(File directory) {
		this.directory = directory;
	}
	
	public void sendMessage(Object object) {
		sendOutput(object.toString());
	}

	public void sendInfoMessage(Object object) {
		sendOutput("[INFO] " + object.toString());
	}

	public void sendWarningMessage(Object object) {
		sendOutput("[WARNING] " + object.toString());
	}

	public void sendErrorMessage(Object object) {
		sendOutput("[ERROR] " + object.toString());
	}
	
	private void sendOutput(String output) {
		ConsoleOutputEvent event = new ConsoleOutputEvent(this, output);
		EventHandler.executeEvent(EventType.CONSOLEOUTPUTEVENT, event);
		if(!event.isCancled()) {
			System.out.println(event.getOutput());
		}
	}

}
