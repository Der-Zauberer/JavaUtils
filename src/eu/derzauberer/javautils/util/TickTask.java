package eu.derzauberer.javautils.util;

import java.util.function.Consumer;

import eu.derzauberer.javautils.services.TimeTickService;

/**
 * This is a task for the {@link TimeTickService} that is called in
 * regular intervals.
 */
public class TickTask {

	private final Consumer<TickTask> consumer;
	private final int ticks;
	private final int repeats;
	private final boolean endless;
	private boolean stopped;
	private int ticksLeft;
	private int repeatsLeft;

	/**
	 * Creates a new task with a consumer that is called ever tick.
	 * 
	 * @param consumer the consumer that is called ever tick
	 * @param repeats  amount of repeats before the task stops executing
	 */
	public TickTask(Consumer<TickTask> consumer, int repeats) {
		this(consumer, repeats, false, 0);
	}

	/**
	 * Creates a new task with a consumer that is called ever tick.
	 * 
	 * @param consumer the consumer that is called ever tick
	 * @param endless  if the task will repeat endless
	 */
	public TickTask(Consumer<TickTask> consumer, boolean endless) {
		this(consumer, 0, endless, 0);
	}

	/**
	 * Creates a new task with a consumer that is called every few
	 * ticks.
	 * 
	 * @param consumer the consumer that is called ever tick
	 * @param repeats  amount of repeats before the task stops executing
	 * @param ticks    pause the task for that amount of ticks
	 */
	public TickTask(Consumer<TickTask> consumer, int repeats, int ticks) {
		this(consumer, repeats, false, ticks);
	}

	/**
	 * Creates a new task with a consumer that is called every few
	 * ticks.
	 * 
	 * @param consumer the consumer that is called ever tick
	 * @param endless  if the task will repeat endless
	 * @param ticks    pause the task for that amount of ticks
	 */
	public TickTask(Consumer<TickTask> consumer, boolean endless, int ticks) {
		this(consumer, 0, endless, ticks);
	}

	/**
	 * Creates a new task with a consumer that is called every few
	 * ticks.
	 * 
	 * @param consumer the consumer that is called ever tick
	 * @param repeats  amount of repeats before the task stops executing
	 * @param endless  if the task will repeat endless
	 * @param ticks    pause the task for that amount of ticks
	 */
	private TickTask(Consumer<TickTask> consumer, int repeats, boolean endless, int ticks) {
		this.consumer = consumer;
		this.ticks = ticks;
		this.repeats = repeats;
		this.endless = endless;
		this.stopped = false;
		this.ticksLeft = this.ticks - 1;
		this.repeatsLeft = this.repeats - 1;
	}

	/**
	 * Returns the {@link Consumer}, which is called in regular intervals
	 * by the {@link TimeTickService}.
	 * 
	 * @return the consumer which is called in regular intervals
	 */
	public Consumer<TickTask> getConsumer() {
		return consumer;
	}

	/**
	 * Returns the amount of ticks between the actions
	 * 
	 * @return the amount of ticks between the actions
	 * @see {@link #getTicksLeft()} {@link #getTickCount()}
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * Returns the amount repeats for the task. It defines how many times
	 * the consumer gets called in total. Please use
	 * {@link #getTicksLeft()} if you want to know how many times are left
	 * or {@link #getRepeatCounter()} if you want to now how many times
	 * the consumer was called.
	 * 
	 * @return the amount of ticks between the actions
	 * @see {@link #getTicksLeft()}, {@link #getRepeatCounter()}
	 */
	public int getRepeats() {
		return repeats;
	}

	/**
	 * Returns if the task repeats endless.
	 * 
	 * @return if the task repeats endless
	 */
	public boolean isEndless() {
		return endless;
	}

	/**
	 * Removes the task event if repeats are left.
	 */
	public void stop() {
		stopped = true;
	}

	/**
	 * Returns if the task is removed even if repeats are left.
	 * 
	 * @return if the task is removed even if repeats are left
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * Decrements the amount of ticks before the consumer will be called
	 * again.
	 * 
	 * @return if ticks are left
	 */
	public boolean decrementTicks() {
		if (ticksLeft > 0) {
			ticksLeft--;
			return false;
		} else {
			ticksLeft = ticks;
			return true;
		}
	}

	/**
	 * Returns the amount of ticks left before the consumer will be
	 * called.
	 * 
	 * @return the amount of ticks left before the consumer will be called
	 */
	public int getTicksLeft() {
		return ticksLeft;
	}

	/**
	 * Returns the amount of ticks passed since the last execution
	 * of the {@link Consumer}.
	 * 
	 * @return the amount of ticks passed since the last execution
	 */
	public int getTickCount() {
		return ticks - ticksLeft;
	}

	/**
	 * Decrements the amount of repeats the consumer will be called again.
	 * 
	 * @return if repeats are left
	 */
	public boolean decrementRepeats() {
		if (repeatsLeft > 0) {
			repeatsLeft--;
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns the amount of repeats the consumer will be called.
	 * 
	 * @return the amount of repeats the consumer will be called
	 */
	public int getRepeatsLeft() {
		return repeatsLeft;
	}

	/**
	 * Returns the amount of repeats since the start of the task.
	 * 
	 * @return the amount of repeats since the start of the task
	 */
	public long getRepeatCounter() {
		return repeats - repeatsLeft;
	}
	
}