package eu.derzauberer.javautils.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import eu.derzauberer.javautils.util.TickTask;

/**
 * The class keeps calling a {@link Consumer} but waits a specific
 * amount of time between the calls. The time of one iteration is
 * called a tick.
 * 
 * @see {@link Consumer}
 */
public class TimeTickService {
	
	private boolean isRunning;
	private boolean restart;
	private long tickSpeed;
	private Timer timer;
	private TimerTask timertask;
	
	private final ArrayList<TickTask> tasks = new ArrayList<>();
	private final HashMap<TickTask, Integer> asyncTasks = new HashMap<>();
	
	/**
	 * Creates a new {@link TimeTickService} with a tickSpeed of 1000
	 * milliseconds. The tickSpeed is the time between the ticks.
	 */
	public TimeTickService() {
		this(1000);
	}

	/**
	 * Creates a new {@link TimeTickService}.
	 * 
	 * @param tickSpeed the time between the ticks
	 */
	public TimeTickService(int tickSpeed) {
		isRunning = false;
		restart = false;
		this.tickSpeed = tickSpeed;
	}
	
	/**
	 * Starts the {@link TimeTickService}, so that it keeps calling the
	 * {@link TickTask} tasks.
	 */
	public void start() {
		if (!isRunning) {
			isRunning = true;
			timer = new Timer();
			timertask = new TimerTask() {
				@Override
				public void run() {
					if(restart) {
						restart = false;
						timertask.cancel();
						isRunning = false;
						start();
					} else {
						onTick();
					}
				}
			};
			timer.schedule(timertask, 0, tickSpeed);
			asyncTasks.keySet().forEach((task) -> createAsyncTask(task, asyncTasks.get(task)));
		}
	}
	
	/**
	 * Stops the {@link TimeTickService}. It can be restarted with
	 * {@link #start()}.
	 */
	public void stop() {
		if (isRunning) {
			isRunning = false;
			timer.cancel();
		}
	}
	
	/**
	 * Returns if the {@link TimeTickService} is running.
	 * 
	 * @return if the controller is running
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * Adds a new task to the controller.
	 * 
	 * @param task the new task to add
	 */
	public void addTask(TickTask task) {
		tasks.add(task);
	}

	/**
	 * Removes a task to the controller.
	 * 
	 * @param task the task to remove
	 */
	public void removeTask(TickTask task) {
		tasks.remove(task);
	}
	
	/**
	 * Adds a new asynchronous task to the controller. The task is independent to the tickSpeed.
	 * 
	 * @param task the new asynchronous task to add
	 */
	public void addAsyncTask(TickTask task, int tickSpeed) {
		createAsyncTask(task, tickSpeed);
	}
	
	/**
	 * Removes an asynchronous task to the controller.
	 * 
	 * @param task the asynchronous task to remove
	 */
	public void removeAsyncTask(TickTask task) {
		task.stop();
		asyncTasks.remove(task);
	}
	
	/**
	 * Sets the tickSpeed of the controller. The tickSpeed is the time
	 * between the ticks.
	 * 
	 * @param tickSpeed the time between the ticks
	 */
	public void setTickSpeed(long tickSpeed) {
		this.tickSpeed = tickSpeed;
		restart = true;
	}
	
	/**
	 * Returns the tickSpeed of the controller. The tickSpeed is the time
	 * between the ticks.
	 * 
	 * @return the tickSpeed of the controller
	 */
	public long getTickSpeed() {
		return tickSpeed;
	}
	
	private void onTick() {
		final ArrayList<TickTask> deleteTasks = new ArrayList<>();
		for(TickTask task : tasks) {
			if(task.decrementTicks() && !task.isStopped()) {
				task.getConsumer().accept(task);
				if(!task.isEndless() && task.decrementRepeats()) {
					deleteTasks.add(task);
				}
			} 
			if(task.isStopped() && !deleteTasks.contains(task)) {
				deleteTasks.add(task);
			}
		}
		for (TickTask task : deleteTasks) {
			tasks.remove(task);
		}
	}
	
	private void createAsyncTask(TickTask task, int tickSpeed) {
		final TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (task.decrementTicks() && !task.isStopped()) {
					task.getConsumer().accept(task);
					if(!task.isEndless() && task.decrementRepeats()) {
						task.stop();
					}
				}
				if (task.isStopped()) {
					asyncTasks.remove(task);
					this.cancel();
				}
			}
		};
		asyncTasks.put(task, tickSpeed);
		if (isRunning) timer.schedule(timerTask, 0, tickSpeed);
	}

}