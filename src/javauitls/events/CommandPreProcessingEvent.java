package javauitls.events;

import javautils.util.Command;
import javautils.util.Console;
import javautils.util.Event;

public class CommandPreProcessingEvent extends Event {

	private boolean cancled;
	private Console console;
	private Command command;
	private String string;
	private String label;
	private String args[];
	
	public CommandPreProcessingEvent(Console console, Command command, String string, String label, String args[]) {
		this.cancled = false;
		this.console = console;
		this.command = command;
		this.string = string;
		this.label = label;
		this.args = args;
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