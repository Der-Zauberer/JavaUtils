package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.CommandExecutionFailedEvent;

public interface CommandExecutionFailedAction {
	
	public abstract void onAction(CommandExecutionFailedEvent event);

}
