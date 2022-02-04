package eu.derzauberer.javautils.util;

import eu.derzauberer.javautils.action.TickTaskAction;

public class TickTask {
	
	private TickTaskAction action;
	private int ticks;
	private int repeats;
	private boolean endless;
	private boolean removed;
	private int ticksleft;
	private int repeatsletft;
	
	public TickTask(TickTaskAction action, int ticks) {
		this(action, ticks, 0);
	}
	
	public TickTask(TickTaskAction action, int ticks, int repeats) {
		this(action, ticks, repeats, false);
	}
	
	public TickTask(TickTaskAction action, int ticks, boolean endless) {
		this(action, ticks, 0, endless);
	}
	
	public TickTask(TickTaskAction action, int ticks, int repeats, boolean endless) {
		this.action = action;
		this.ticks = ticks;
		this.repeats = repeats;
		this.endless = endless;
		this.removed = false;
		this.ticksleft = this.ticks;
		this.repeatsletft = this.repeats;
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
		if(ticksleft > 1) {
			ticksleft--;
			return false;
		} else {
			ticksleft = ticks;
			return true;
		}
	}
	
	public int getTicksLetft() {
		return ticksleft;
	}
	
	public boolean decrementRepeats() {
		if(repeatsletft > 1) {
			repeatsletft--;
			return false;
		} else {
			return true;
		}
	}
	
	public int getRepeatsLetft() {
		return repeatsletft;
	}
	
}