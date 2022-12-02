package eu.derzauberer.javautils.util;

import java.util.List;

/**
 * This interface can be implemented in an object, which changes its
 * data in any way. The observable will call observers, which are
 * applied to that observer, as soon, as the object changes by the
 * {@link #updateObserver()} method.
 */
public interface Observable {

	/**
	 * Returns all observers to add and remove them.
	 * 
	 * @return a list of all active observers
	 */
	List<Observer> getObserverList();

	/**
	 * Updates every observer in the {@link #getObserverList()} list by
	 * calling the {@link EventObserver#update()} method.
	 */
	default void updateObserver() {
		for (Observer observer : getObserverList()) {
			observer.update();
		}
	}

}
