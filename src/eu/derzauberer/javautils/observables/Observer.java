package eu.derzauberer.javautils.observables;

/**
 * The observer is called whenever an update happens for the object in
 * which the observer is implemented.
 */
public interface Observer {
	
	/**
	 * The update function of the observer is called whenever an update
	 * happens for the object in which the observer is implemented.
	 */
	void update();

}
