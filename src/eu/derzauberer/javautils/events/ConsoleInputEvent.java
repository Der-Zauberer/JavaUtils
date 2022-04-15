package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.handler.ConsoleHandler;

public class ConsoleInputEvent extends Event {
	
	private ConsoleHandler console;
	private String input;
	
	public ConsoleInputEvent(ConsoleHandler console, String input) {
		this.console = console;
		this.input = input;
		execute();
	}
	
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}
	
	public ConsoleHandler getConsole() {
		return console;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
	
	public String getInput() {
		return input;
	}

}
