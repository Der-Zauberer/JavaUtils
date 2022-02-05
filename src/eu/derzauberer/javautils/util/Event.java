package eu.derzauberer.javautils.util;

public abstract class Event {
	
	public enum EventPriority{HIGHEST, HIGHT, NORMAL, LOW, LOWEST};
	
	private boolean cancelled;
	
	protected void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
}