package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.CommandNotFoundEvent;

public interface CommandNotFoundAction {

	void onAction(CommandNotFoundEvent event);
	
}
