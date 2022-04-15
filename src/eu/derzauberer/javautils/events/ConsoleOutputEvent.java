package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.handler.ConsoleHandler;
import eu.derzauberer.javautils.handler.ConsoleHandler.MessageType;

public class ConsoleOutputEvent extends Event {
	
	private ConsoleHandler console;
	private String output;
	private MessageType type;
	
	public ConsoleOutputEvent(ConsoleHandler console, String output, MessageType type) {
		this.console = console;
		this.output = output;
		this.type = type;
		execute();
	}
	
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}
	
	public ConsoleHandler getConsole() {
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
