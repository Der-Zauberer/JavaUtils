package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.handler.LoopHandler;

public class LoopTickEvent extends Event {
	
	private LoopHandler handler;
	private int nanoDeltaTime;
	private int ticksPerSecond;
	
	public LoopTickEvent(LoopHandler handler, int nanoDeltaTime, int ticksPerSecond) {
		super();
		this.handler = handler;
		this.nanoDeltaTime = nanoDeltaTime;
		this.ticksPerSecond = ticksPerSecond;
	}
	
	public LoopHandler getHandler() {
		return handler;
	}
	
	public int getNanoDeltaTime() {
		return nanoDeltaTime;
	}
	
	public int getTicksPerSecond() {
		return ticksPerSecond;
	}

}
