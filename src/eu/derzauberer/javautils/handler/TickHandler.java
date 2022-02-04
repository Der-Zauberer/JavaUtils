package eu.derzauberer.javautils.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import eu.derzauberer.javautils.util.TickTask;

public class TickHandler {
	
	private static boolean isRunning = false;
	private static long tickspeed = 1000;
	private static Timer timer = new Timer();
	private static TimerTask timertask;
	private static boolean restart = false;
	
	private static ArrayList<TickTask> tasks = new ArrayList<>();
	private static HashMap<TickTask, TimerTask> asyncTasks = new HashMap<>();
	private static HashMap<TickTask, Integer> asyncSpeed = new HashMap<>();
	
	public static void start() {
		if (!isRunning) {
			isRunning = true;
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
			timer.schedule(timertask, 0, tickspeed);
			asyncTasks.keySet().forEach((task) -> createAsyncTask(task, asyncTasks.get(task), asyncSpeed.get(task)));
		}
	}
	
	public static void stop() {
		if (isRunning) {
			timer.cancel();
		}
	}
	
	public static boolean isRunning() {
		return isRunning;
	}
	
	public static void addTask(TickTask task) {
		tasks.add(task);
	}
	
	public static void removeTask(TickTask task) {
		tasks.remove(task);
	}
	
	public static void addAsyncTask(TickTask task, int tickspeed) {
		createAsyncTask(task, null, tickspeed);
	}
	
	public static void removeAsyncTask(TickTask task) {
		asyncTasks.get(task).cancel();
		asyncTasks.remove(task);
		asyncSpeed.remove(task);
	}
	
	public static void setTickSpeed(long tickspeed) {
		TickHandler.tickspeed = tickspeed;
		restart = true;
	}
	
	public static long getTickspeed() {
		return tickspeed;
	}
	
	private static void onTick() {
		ArrayList<TickTask> deletetasks = new ArrayList<>();
		for(TickTask task : tasks) {
			if(task.decrementTicks() && !task.isRemoved()) {
				task.getAction().run(task);
				if(!task.isEndless() && task.decrementRepeats()) {
					deletetasks.add(task);
				}
			} 
			if(task.isRemoved() && !deletetasks.contains(task)) {
				deletetasks.add(task);
			}
		}
		for (TickTask task : deletetasks) {
			tasks.remove(task);
		}
	}
	
	private static void createAsyncTask(TickTask task, TimerTask timerTask, int tickspeed) {
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (task.decrementTicks() && !task.isRemoved()) {
					task.getAction().run(task);
					if(!task.isEndless() && task.decrementRepeats()) {
						task.remove();
					}
				}
				if (task.isRemoved()) {
					asyncTasks.remove(task);
					asyncSpeed.remove(task);
					this.cancel();
				}
			}
		};
		asyncTasks.put(task, timerTask);
		asyncSpeed.put(task, tickspeed);
		timer.schedule(timerTask, 0, tickspeed);
	}
	
	protected static Timer getTimer() {
		return timer;
	}

}