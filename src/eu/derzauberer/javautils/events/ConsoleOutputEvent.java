package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Console;
import eu.derzauberer.javautils.util.Event;
import eu.derzauberer.javautils.util.Console.MessageType;

public class ConsoleOutputEvent extends Event {
	
	private Console console;
	private String output;
	private MessageType type;
	
	public ConsoleOutputEvent(Console console, String output, MessageType type) {
		this.console = console;
		this.output = output;
		this.type = type;
		execute();
	}
	
	public void setCancelled(boolean cancelled) {
		this.setCancelled(cancelled);
	}
	
	public Console getConsole() {
		return console;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public String getOutput() {
		return output;
	}

	public void setMessageType(MessageType type) {
		this.type = type;
	}

	public MessageType getMessageType() {
		return type;
	}

}
