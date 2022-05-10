package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ConsoleOutputEvent;

public interface ConsoleOutputAction {

	public abstract void onAction(ConsoleOutputEvent event);
	
}
