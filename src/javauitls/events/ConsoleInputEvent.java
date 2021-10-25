package javauitls.events;

import javautils.util.Console;
import javautils.util.Event;

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
	
	public void setConsole(Console console) {
		this.console = console;
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
