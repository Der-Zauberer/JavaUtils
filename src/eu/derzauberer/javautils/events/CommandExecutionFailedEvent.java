package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Sender;

public class CommandExecutionFailedEvent extends Event {

	private Sender sender;
	private Command command;
	private String string;
	private String label;
	private String args[];
	
	public CommandExecutionFailedEvent(Sender sender, Command command, String string, String label, String args[]) {
		this.sender = sender;
		this.command = command;
		this.string = string;
		this.label = label;
		this.args = args;
		execute();
	}
	
	public Sender getSender() {
		return sender;
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
