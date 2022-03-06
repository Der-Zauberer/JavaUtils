package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Console;

public class ConsoleInputEvent extends Event {
	
	private Console console;
	private String input;
	
	public ConsoleInputEvent(Console console, String input) {
		this.console = console;
		this.input = input;
		execute();
	}
	
	public void setCancelled(boolean cancelled) {
		this.setCancelled(cancelled);
	}
	
	public Console getConsole() {
		return console;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
	
	public String getInput() {
		return input;
	}

}
