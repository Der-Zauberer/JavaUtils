package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.service.LoopTickService;

/**
 * This event gets called every time, the {@link LoopTickService} ticks.
 */
public class TickEvent extends Event {
	
	private final long deltaTimeNanos;
	
	/**
	 * Creates a new event that gets called every time, the {@link LoopTickService} ticks.
	 * 
	 * @param deltaTimeNanos the time between the ticks in nanoseconds
	 */
	public TickEvent(long deltaTimeNanos) {
		this.deltaTimeNanos = deltaTimeNanos;
	}
	
	/**
	 * Returns the time between the ticks in nanoseconds.
	 * 
	 * @return the time between the ticks in nanoseconds
	 */
	public long getDeltaTimeNanos() {
		return deltaTimeNanos;
	}
	
	/**
	 * Returns the time between the ticks in microseconds.
	 * 
	 * @return the time between the ticks in microseconds
	 */
	public long getDeltaTimeMicros() {
		return deltaTimeNanos / 1000;
	}
	
	/**
	 * Returns the time between the ticks in milliseconds.
	 * 
	 * @return the time between the ticks in milliseconds
	 */
	public long getDeltaTimeMillis() {
		return deltaTimeNanos / 1000000;
	}
	
	/**
	 * Returns how many times the tick was called in the last second.
	 * 
	 * @return how many times the tick was called in the last second
	 */
	public int getTicksPerSecond() {
		return (int) (1000000000 / (double) deltaTimeNanos);
	}

}
