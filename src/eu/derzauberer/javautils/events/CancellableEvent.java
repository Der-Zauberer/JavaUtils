package eu.derzauberer.javautils.events;

/**
 * The {@link Event} represents an event. An event contains data about specific
 * occurrences. Events can also manipulate the data for the execution. This
 * event is cancellable. The event will execute nothing after the event call if
 * the event is cancelled.
 */
public abstract class CancellableEvent extends Event {

	private boolean cancelled;
	
	/**
	 * The event will execute nothing after the event call if the event is
	 * cancelled.
	 * 
	 * @param cancelled if the event should be cancelled
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	/**
	 * Returns if the event was cancelled. The event will execute nothing after the
	 * event call if the event is cancelled.
	 * @return if the event is cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
}
