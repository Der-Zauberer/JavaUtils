package eu.derzauberer.javautils.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import eu.derzauberer.javautils.util.TickTask;

public class TickHandler {
	
	private boolean isRunning;
	private boolean restart;
	private long tickspeed;
	private Timer timer;
	private TimerTask timertask;
	
	private final ArrayList<TickTask> tasks = new ArrayList<>();
	private final HashMap<TickTask, Integer> asyncTasks = new HashMap<>();
	
	public TickHandler() {
		this(1000);
	}
	
	public TickHandler(int tickspeed) {
		isRunning = false;
		restart = false;
		this.tickspeed = tickspeed;
	}
	
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
			timer.schedule(timertask, 0, tickspeed);
			asyncTasks.keySet().forEach((task) -> createAsyncTask(task, asyncTasks.get(task)));
		}
	}
	
	public void stop() {
		if (isRunning) {
			isRunning = false;
			timer.cancel();
		}
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public void addTask(TickTask task) {
		tasks.add(task);
	}
	
	public void removeTask(TickTask task) {
		tasks.remove(task);
	}
	
	public void addAsyncTask(TickTask task, int tickspeed) {
		createAsyncTask(task, tickspeed);
	}
	
	public void removeAsyncTask(TickTask task) {
		task.remove();
		asyncTasks.remove(task);
	}
	
	public void setTickSpeed(long tickspeed) {
		this.tickspeed = tickspeed;
		restart = true;
	}
	
	public long getTickspeed() {
		return tickspeed;
	}
	
	private void onTick() {
		final ArrayList<TickTask> deletetasks = new ArrayList<>();
		for(TickTask task : tasks) {
			if(task.decrementTicks() && !task.isRemoved()) {
				task.getAction().accept(task);
				if(!task.isEndless() && task.decrementRepeats()) {
					deletetasks.add(task);
				} else {
					task.incrementRepeatCounter();
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
	
	private void createAsyncTask(TickTask task, int tickspeed) {
		final TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (task.decrementTicks() && !task.isRemoved()) {
					task.getAction().accept(task);
					if(!task.isEndless() && task.decrementRepeats()) {
						task.remove();
					} else {
						task.incrementRepeatCounter();
					}
				}
				if (task.isRemoved()) {
					asyncTasks.remove(task);
					this.cancel();
				}
			}
		};
		asyncTasks.put(task, tickspeed);
		if (isRunning) timer.schedule(timerTask, 0, tickspeed);
	}
	
	protected Timer getTimer() {
		return timer;
	}

}