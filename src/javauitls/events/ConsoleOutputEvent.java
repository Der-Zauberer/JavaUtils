package javauitls.events;

import javautils.util.Console;
import javautils.util.Event;

public class ConsoleOutputEvent extends Event {
	
	private boolean cancled;
	private Console console;
	private String output;
	
	public ConsoleOutputEvent(Console console, String output) {
		this.cancled = false;
		this.console = console;
		this.output = output;
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
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public String getOutput() {
		return output;
	}

}
