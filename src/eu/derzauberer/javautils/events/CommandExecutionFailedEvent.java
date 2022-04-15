package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.handler.ConsoleHandler;
import eu.derzauberer.javautils.util.Command;

public class CommandExecutionFailedEvent extends Event {

	private ConsoleHandler console;
	private Command command;
	private String string;
	private String label;
	private String args[];
	
	public CommandExecutionFailedEvent(ConsoleHandler console, Command command, String string, String label, String args[]) {
		this.console = console;
		this.command = command;
		this.string = string;
		this.label = label;
		this.args = args;
		execute();
	}
	
	public ConsoleHandler getConsole() {
		return console;
	}
	
	public Command getCommand() {
		return command;
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
