package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.controller.ConsoleController;

public class ConsoleInputEvent extends CancellableEvent {
	
	private final ConsoleController console;
	private String input;
	
	public ConsoleInputEvent(ConsoleController console, String input) {
		this.console = console;
		this.input = input;
	}
	
	public ConsoleController getConsole() {
		return console;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
	
	public String getInput() {
		return input;
	}

}
