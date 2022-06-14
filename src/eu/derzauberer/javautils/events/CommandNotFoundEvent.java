package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Sender;

public class CommandNotFoundEvent extends Event {
	
	private final Sender sender;
	private final String string;
	private final String label;
	private final String args[];
	
	public CommandNotFoundEvent(Sender sender, String string, String label, String args[]) {
		this.sender = sender;
		this.string = string;
		this.label = label;
		this.args = args;
		execute();
	}
	
	public Sender getSender() {
		return sender;
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
