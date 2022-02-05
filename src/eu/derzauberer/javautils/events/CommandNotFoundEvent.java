package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Console;
import eu.derzauberer.javautils.util.Event;

public class CommandNotFoundEvent extends Event {
	
	private Console console;
	private String string;
	private String label;
	private String args[];
	
	public CommandNotFoundEvent(Console console, String string, String label, String args[]) {
		this.console = console;
		this.string = string;
		this.label = label;
		this.args = args;
		execute();
	}
	
	public Console getConsole() {
		return console;
	}
	
	public String getString() {
		return string;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String[] getArgs() {
		return args;
	}

}
