package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.CommandPreProcessingEvent;

public interface CommandPreProcessingAction {
	
	public abstract void onAction(CommandPreProcessingEvent event);

}
