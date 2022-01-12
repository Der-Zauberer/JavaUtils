package eu.derzauberer.javautils.handler;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import eu.derzauberer.javautils.util.TickTask;

public class TickHandler {
	
	private static long tickspeed = 1000;
	private static Timer timer = new Timer();
	private static TimerTask timertask;
	private static boolean restart = false;
	
	private static ArrayList<TickTask> tasks = new ArrayList<>();
	
	public static void runTickHandler() {
		timertask = new TimerTask() {
			@Override
			public void run() {
				if(restart) {
					restart = false;
					timertask.cancel();
					runTickHandler();
				} else {
					onTick();
				}
			}
		};
		timer.schedule(timertask, 0, tickspeed);
	}
	
	public static void stopTickHandler() {
		timer.cancel();
	}
	
	public static void addTask(TickTask task) {
		tasks.add(task);
	}
	
	public static void removeTask(TickTask task) {
		tasks.remove(task);
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

}