package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Sender;

public class ConsoleInputEvent extends Event {
	
	private Sender sender;
	private String input;
	
	public ConsoleInputEvent(Sender sender, String input) {
		this.sender = sender;
		this.input = input;
		execute();
	}
	
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}
	
	public Sender getSender() {
		return sender;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
	
	public String getInput() {
		return input;
	}

}
