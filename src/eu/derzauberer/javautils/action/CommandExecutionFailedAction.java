package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.CommandExecutionFailedEvent;

public interface CommandExecutionFailedAction {
	
	void onAction(CommandExecutionFailedEvent event);

}
