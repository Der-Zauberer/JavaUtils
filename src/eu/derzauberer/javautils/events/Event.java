package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.handler.EventHandler;

public abstract class Event {
	
	public enum EventPriority{HIGHEST, HIGHT, NORMAL, LOW, LOWEST};
	
	private boolean cancelled;
	
	protected void execute() {
		EventHandler.executeEvent(this);
	}
	
	protected void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
}