package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Console;

public class CommandPreProcessingEvent extends Event {

	private Console console;
	private Command command;
	private String string;
	private String label;
	private String args[];
	
	public CommandPreProcessingEvent(Console console, Command command, String string, String label, String args[]) {
		this.console = console;
		this.command = command;
		this.string = string;
		this.label = label;
		this.args = args;
		execute();
	}
	
	public void setCancelled(boolean cancelled) {
		this.setCancelled(cancelled);
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
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setArgs(String[] args) {
		this.args = args;
	}
	
	public String[] getArgs() {
		return args;
	}
	
}
