package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Sender;

public class CommandNotFoundEvent extends Event {
	
	private Sender sender;
	private String string;
	private String label;
	private String args[];
	
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
