package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.CommandNotFoundEvent;

public interface CommandNotFoundAction {

	public abstract void onAction(CommandNotFoundEvent event);
	
}
