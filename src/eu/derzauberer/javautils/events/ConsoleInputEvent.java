package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Console;
import eu.derzauberer.javautils.util.Event;

public class ConsoleInputEvent extends Event {
	
	private boolean cancled;
	private Console console;
	private String input;
	
	public ConsoleInputEvent(Console console, String input) {
		this.cancled = false;
		this.console = console;
		this.input = input;
	}
	
	public void setCancled(boolean cancled) {
		this.cancled = cancled;
	}
	
	public boolean isCancled() {
		return cancled;
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
