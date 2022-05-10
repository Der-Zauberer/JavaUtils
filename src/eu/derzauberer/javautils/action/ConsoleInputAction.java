package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.ConsoleInputEvent;

public interface ConsoleInputAction {

	public abstract void onAction(ConsoleInputEvent event);
	
}
