package eu.derzauberer.javautils.util;

import eu.derzauberer.javautils.action.TickTaskAction;

public class TickTask {
	
	private TickTaskAction action;
	private int ticks;
	private int repeats;
	private boolean endless;
	private boolean removed;
	private int ticksLeft;
	private int repeatsLetft;
	private long repeatCounter;
	
	public TickTask(TickTaskAction action, int ticks) {
		this(action, ticks, 0, false);
	}
	
	public TickTask(TickTaskAction action, int ticks, int repeats) {
		this(action, ticks, repeats, false);
	}
	
	public TickTask(TickTaskAction action, int ticks, boolean endless) {
		this(action, ticks, 0, endless);
	}
	
	private TickTask(TickTaskAction action, int ticks, int repeats, boolean endless) {
		this.action = action;
		this.ticks = ticks;
		this.repeats = repeats;
		this.endless = endless;
		this.removed = false;
		this.ticksLeft = this.ticks;
		this.repeatsLetft = this.repeats;
		this.repeatCounter = 0;
	}
	
	public TickTaskAction getAction() {
		return action;
	}
	
	public int getTicks() {
		return ticks;
	}
	
	public int getRepeats() {
		return repeats;
	}
	
	public boolean isEndless() {
		return endless;
	}
	
	public void remove() {
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public boolean decrementTicks() {
		if(ticksLeft > 1) {
			ticksLeft--;
			return false;
		} else {
			ticksLeft = ticks;
			return true;
		}
	}
	
	public int getTicksLetft() {
		return ticksLeft;
	}
	
	public boolean decrementRepeats() {
		if(repeatsLetft > 1) {
			repeatsLetft--;
			return false;
		} else {
			return true;
		}
	}
	
	public int getRepeatsLetft() {
		return repeatsLetft;
	}
	
	public void incrementRepeatCounter() {
		if (repeatCounter < Long.MAX_VALUE) {
			repeatCounter++;
		} else {
			repeatCounter = 0;
		}
	}
	
	public long getRepeatCounter() {
		return repeatCounter;
	}
	
}