package eu.derzauberer.javautils.controller;

import java.util.function.Consumer;

/**
 * The class keeps calling a {@link Consumer} and measures the time
 * between the calls.
 * 
 * @see {@link Consumer}
 */
public class LoopController {
	
	private Thread thread;
	private Consumer<LoopController> consumer;
	private boolean isRunning;
	private long deltaTimeNanos;
	private long lastTimestamp;
	
	/**
	 * Creates a new {@link LoopController} with the {@link Consumer}, which
	 * will be called every iteration.
	 * 
	 * @param Consumer the consumer, which will be called every iteration
	 */
	public LoopController(Consumer<LoopController> consumer) {
		this.consumer = consumer;
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
			consumer.accept(this);
		}
	}
	
	/**
	 * Starts the {@link LoopController}, so that it keeps calling the
	 * {@link Consumer} and measures the time between the calls.
	 */
	public void start() {
		if (!isRunning) {
			isRunning = true;
			thread = new Thread(this::loop);
			thread.start();
		}
	}
	
	/**
	 * Stops the {@link LoopController}. It can be restarted with
	 * {@link #start()}.
	 */
	public void stop() {
		if (isRunning) {
			isRunning = false;
			thread.interrupt();
		}
	}
	
	/**
	 * Returns if the {@link LoopController} is running.
	 * 
	 * @return if the controller is running
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * Returns the time between the {@link Consumer} calls in nanoseconds.
	 * 
	 * @return the time between the consumer calls in nanoseconds
	 */
	public int getTicksPerSecond() {
		return (int) (1000000000 / (double) deltaTimeNanos);
	}
	
	/**
	 * Returns the time between the {@link Consumer} calls in microseconds.
	 * 
	 * @return the time between the consumer calls in microseconds
	 */
	public long getDeltaTimeNanos() {
		return deltaTimeNanos;
	}
	
	/**
	 * Returns the time between the {@link Consumer} calls in milliseconds.
	 * 
	 * @return the time between the consumer calls in milliseconds
	 */
	public long getDeltaTimeMycros() {
		return deltaTimeNanos / 1000;
	}
	
	/**
	 * Returns how many times the {@link Consumer} was called in the last second.
	 * 
	 * @return how many times the consumer was called in the last second
	 */
	public long getDeltaTimeMillis() {
		return deltaTimeNanos / 1000000;
	}
	
}
