package javauitls.events;

import javautils.util.Console;
import javautils.util.Event;

public class CommandNotFoundEvent extends Event {
	
	private Console console;
	private String command;
	private String label;
	private String args[];
	
	public CommandNotFoundEvent(Console console, String command, String label, String args[]) {
		this.console = console;
		this.command = command;
		this.label = label;
		this.args = args;
	}
	
	public Console getConsole() {
		return console;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String[] getArgs() {
		return args;
	}

}
