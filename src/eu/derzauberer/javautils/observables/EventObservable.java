package eu.derzauberer.javautils.observables;

import java.util.List;
import eu.derzauberer.javautils.events.Event;

/**
 * This interface can be implemented in an object, which changes its
 * data in any way. The observable will call observers, which are
 * applied to that observer, as soon, as the object changes by the
 * {@link #updateObserver(Event)} method.
 */
public interface EventObservable {

	/**
	 * Returns all observers to add and remove them.
	 * 
	 * @return a list of all active observers
	 */
	List<EventObserver> getObserverList();

	/**
	 * Updates every observer in the {@link #getObserverList()} list by
	 * calling the {@link EventObserver#update(Event)} method.
	 * 
	 * @param event the event, which is important for the observers to
	 *              check what happened and what have changed
	 */
	default void updateObserver(Event event) {
		for (EventObserver observer : getObserverList()) {
			observer.update(event);
		}
	}

}
