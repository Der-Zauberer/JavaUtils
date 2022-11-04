package eu.derzauberer.javautils.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import eu.derzauberer.javautils.events.Event;
import eu.derzauberer.javautils.util.EventListener;

/**
 * The controller deals with incoming events and calls the corresponding
 * {@link EventListener} to do specific things when called.
 */
public class EventController {
	
	private final List<EventListener<?>> listeners = new ArrayList<>();
	private static final EventController globalEventController = new EventController();
	
	/**
	 * Adds a listener to the controller. The listener will be called when a event
	 * occurs.
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(EventListener<?> listener) {
		listeners.add(listener);
		Collections.sort(listeners);
	}

	/**
	 * Removes a listener form the controller.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(EventListener<?> listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Returns an unmodifiable list of all added events of the controller. The
	 * events are sorted by their priority.
	 * 
	 * @return an unmodifiable list of events
	 */
	public List<EventListener<?>> getListeners() {
		return Collections.unmodifiableList(listeners);
	}
	
	/**
	 * Call all listeners, which are instances of the event.
	 * 
	 * @param event the event, that occurs
	 */
	public void callListeners(Event event) {
		listeners.stream()
			.filter(listener -> listener.getEventType().isAssignableFrom(event.getClass()))
			.forEach(listener -> listener.acceptUnchecked(event));
	}
	
	/**
	 * This is the global event controller, which handles all events that occur.
	 * 
	 * @return the global event controller that handles all events
	 */
	public static EventController getGlobalEventController() {
		return globalEventController;
	}

}
