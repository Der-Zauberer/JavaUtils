package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.CommandPreProcessingEvent;

public interface CommandPreProcessingAction {
	
	void onAction(CommandPreProcessingEvent event);

}
