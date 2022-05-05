package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Sender;

public class ConsoleOutputEvent extends Event {
	
	private Sender sender;
	private String output;
	
	public ConsoleOutputEvent(Sender sender, String output) {
		this.sender = sender;
		this.output = output;
		execute();
	}
	
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}
	
	public Sender getSender() {
		return sender;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public String getOutput() {
		return output;
	}

}
