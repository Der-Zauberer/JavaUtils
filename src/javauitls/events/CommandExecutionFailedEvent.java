package javauitls.events;

import javautils.util.Command;
import javautils.util.Console;
import javautils.util.Event;

public class CommandExecutionFailedEvent extends Event {

	private Console console;
	private Command command;
	private String string;
	private String label;
	private String args[];
	
	public CommandExecutionFailedEvent(Console console, Command command, String string, String label, String args[]) {
		this.console = console;
		this.command = command;
		this.string = string;
		this.label = label;
		this.args = args;
	}
	
	public Console getConsole() {
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
