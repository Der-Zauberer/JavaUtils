package eu.derzauberer.javautils.util;

import java.util.function.Consumer;

public class TickTask {
	
	private final Consumer<TickTask> action;
	private final int ticks;
	private final int repeats;
	private final boolean endless;
	private boolean removed;
	private int ticksLeft;
	private int repeatsLetft;
	private long repeatCounter;
	
	public TickTask(Consumer<TickTask> action, int ticks) {
		this(action, ticks, 0, false);
	}
	
	public TickTask(Consumer<TickTask> action, int ticks, int repeats) {
		this(action, ticks, repeats, false);
	}
	
	public TickTask(Consumer<TickTask> action, int ticks, boolean endless) {
		this(action, ticks, 0, endless);
	}
	
	private TickTask(Consumer<TickTask> action, int ticks, int repeats, boolean endless) {
		this.action = action;
		this.ticks = ticks;
		this.repeats = repeats;
		this.endless = endless;
		this.removed = false;
		this.ticksLeft = this.ticks;
		this.repeatsLetft = this.repeats;
		this.repeatCounter = 0;
	}
	
	public Consumer<TickTask> getAction() {
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