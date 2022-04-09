package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.events.LoopTickEvent;

public interface LoopTickAction {
	
	public abstract void onAction(LoopTickEvent event);

}
