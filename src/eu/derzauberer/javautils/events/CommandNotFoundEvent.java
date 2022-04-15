package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.handler.ConsoleHandler;

public class CommandNotFoundEvent extends Event {
	
	private ConsoleHandler console;
	private String string;
	private String label;
	private String args[];
	
	public CommandNotFoundEvent(ConsoleHandler console, String string, String label, String args[]) {
		this.console = console;
		this.string = string;
		this.label = label;
		this.args = args;
		execute();
	}
	
	public ConsoleHandler getConsole() {
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
