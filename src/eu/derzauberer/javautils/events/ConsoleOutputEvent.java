package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.controller.ConsoleController;
import eu.derzauberer.javautils.util.Sender.MessageType;

public class ConsoleOutputEvent extends CancellableEvent {
	
	private final ConsoleController console;
	private String output;
	private final MessageType type;
	
	public ConsoleOutputEvent(ConsoleController console, String output, MessageType type) {
		this.console = console;
		this.output = output;
		this.type = type;
	}
	
	public ConsoleController getConsole() {
		return console;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public String getOutput() {
		return output;
	}
	
	public MessageType getType() {
		return type;
	}

}
