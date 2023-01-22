package eu.derzauberer.javautils.controller;

import java.util.function.Consumer;

import eu.derzauberer.javautils.events.TickEvent;

/**
 * The class keeps calling a {@link Consumer} and measures the time
 * between the calls. One iteration is called a tick.
 * 
 * @see {@link Consumer}
 */
public class LoopTickController {
	
	private Thread thread;
	private final Consumer<TickEvent> action;
	private boolean isRunning;
	private long deltaTimeNanos;
	private long lastTimestamp;
	
	/**
	 * Creates a new {@link LoopTickController} with the {@link Consumer}, which
	 * will be called every iteration. One iteration is called a tick.
	 * 
	 * @param action the consumer, which will be called every iteration
	 */
	public LoopTickController(Consumer<TickEvent> action) {
		this.action = action;
		isRunning = false;
		deltaTimeNanos = 0;
		lastTimestamp = 0;
	}
	
	/**
	 * Saves the last system time and calls the {@link Consumer}. This is
	 * only one iteration, please start the {@link Thread} to achieve a
	 * loop.
	 */
	private void loop() {
		while (!thread.isInterrupted()) {
			long time = System.nanoTime();
			deltaTimeNanos = time - lastTimestamp;
			lastTimestamp = time;
			final TickEvent event = new TickEvent(deltaTimeNanos);
			action.accept(event);
		}
	}
	
	/**
	 * Starts the {@link LoopTickController}, so that it keeps calling the
	 * {@link Consumer} and measures the time between the calls one iteration is called a tick.
	 */
	public void start() {
		if (!isRunning) {
			isRunning = true;
			thread = new Thread(this::loop);
			thread.start();
		}
	}
	
	/**
	 * Stops the {@link LoopTickController}. It can be restarted with
	 * {@link #start()}.
	 */
	public void stop() {
		if (isRunning) {
			isRunning = false;
			thread.interrupt();
		}
	}
	
	/**
	 * Returns if the {@link LoopTickController} is running.
	 * 
	 * @return if the controller is running
	 */
	public boolean isRunning() {
		return isRunning;
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
