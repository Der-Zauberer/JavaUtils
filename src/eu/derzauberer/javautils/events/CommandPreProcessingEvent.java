package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Sender;

public class CommandPreProcessingEvent extends CancellableEvent {

	private final Sender sender;
	private final Command command;
	private final String string;
	private String label;
	private String args[];
	
	public CommandPreProcessingEvent(Sender sender, Command command, String string, String label, String args[]) {
		this.sender = sender;
		this.command = command;
		this.string = string;
		this.label = label;
		this.args = args;
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
