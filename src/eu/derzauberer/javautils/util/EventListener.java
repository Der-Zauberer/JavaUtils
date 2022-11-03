package eu.derzauberer.javautils.util;

import java.util.function.Consumer;

import eu.derzauberer.javautils.controller.EventController;
import eu.derzauberer.javautils.events.Event;

/**
 * When registered in an {@link EventController}, the listener waits for
 * incoming events and executes the action {@link Consumer}.
 *
 * @param <T> the command, on which occurrence the action executes
 */
public class EventListener<T extends Event> implements Comparable<EventListener<?>> {
	
	/**
	 * Represents the priority of events.
	 */
	public enum EventPriority {

		EARLIEST(0), 
		EARLY(1), 
		NORMAL(2),
		LATE(3),
		LATEST(4);
		
		private final int priority;
		
		private EventPriority(final int priority) {
			this.priority = priority;
		}
		
		public int getPriority() {
			return priority;
		}
		
	}
	
	private final EventPriority priority;
	private final Class<T> eventType;
	private final Consumer<T> action;
	
	/**
	 * Creates a new listener, which when registered in the {@link EventController},
	 * waits for incoming events and executes the action {@link Consumer}.
	 *
	 * @param eventType the class of the event to filter the {@link EventController}
	 * @param action    the consumer that executes when an event occurs
	 */
	public EventListener(Class<T> eventType, Consumer<T> action) {
		this(EventPriority.NORMAL, eventType, action);
	}
	
	/**
	 * Creates a new listener, which when registered in the {@link EventController},
	 * waits for incoming events and executes the action {@link Consumer}.
	 * 
	 * @param priority  when the action should be executed
	 * @param eventType the class of the event to filter the {@link EventController}
	 * @param action    the consumer that executes when an event occurs
	 */
	public EventListener(EventPriority priority, Class<T> eventType, Consumer<T> action) {
		this.priority = priority;
		this.eventType = eventType;
		this.action = action;
	}
	
	/**
	 * Returns the priority of the event. The priority defines when the action
	 * should be executed
	 * 
	 * @return the priority of the event
	 */
	public EventPriority getPriority() {
		return priority;
	}
	
	/**
	 * Returns the class of the event to filter the {@link EventController}.
	 * 
	 * @return the class of the event
	 */
	public Class<T> getEventType() {
		return eventType;
	}
	
	/**
	 * Returns the consumer that executes when an event occurs.
	 * 
	 * @return the consumer that executes when an event occurs
	 */
	public Consumer<T> getAction() {
		return action;
	}
	
	/**
	 * Executes the consumer based on an event.
	 * 
	 * @param event the event that occurred
	 * @throws ClassCastException if the given event is not an instance of the
	 *                            event, which was used for the
	 *                            {@link EventListener}
	 */
	@SuppressWarnings("unchecked")
	public void acceptUnchecked(Event event) {
		action.accept((T) event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(EventListener<?> listener) {
		return priority.getPriority() - listener.getPriority().getPriority();
	}

}
