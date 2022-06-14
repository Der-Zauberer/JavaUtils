package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Console;
import eu.derzauberer.javautils.util.Sender.MessageType;

public class ConsoleOutputEvent extends Event {
	
	private final Console console;
	private String output;
	private final MessageType type;
	
	public ConsoleOutputEvent(Console console, String output, MessageType type) {
		this.console = console;
		this.output = output;
		this.type = type;
		execute();
	}
	
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
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
	
	public MessageType getType() {
		return type;
	}

}
