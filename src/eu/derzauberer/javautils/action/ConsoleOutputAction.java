package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ConsoleOutputEvent;

public interface ConsoleOutputAction {

	void onAction(ConsoleOutputEvent event);
	
}
