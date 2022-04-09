package eu.derzauberer.javautils.action;

import eu.derzauberer.javautils.handler.LoopHandler;

public interface LoopTickAction {
	
	public abstract void onAction(LoopHandler handler, int nanoDeltaTime, int ticksPerSecond);

}
