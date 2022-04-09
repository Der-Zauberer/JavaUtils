package eu.derzauberer.javautils.handler;

import eu.derzauberer.javautils.action.LoopTickAction;

public class LoopHandler {
	
	private Thread thread;
	private LoopTickAction action;
	private int deltaTime;
	private long lastTimestamp;
	
	public LoopHandler(LoopTickAction action) {
		this.action = action;
		deltaTime = 0;
		lastTimestamp = 0;
	}
	
	private void loop() {
		while (!thread.isInterrupted()) {
			long time = System.nanoTime();
			deltaTime = (int) (time - lastTimestamp);
			lastTimestamp = time;
			if (action != null) action.onAction(this, deltaTime, getTicksPerSecond());
		}
		thread = null;
	}
	
	public void start() {
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					loop();
				}
			};
		}
		thread.start();
	}
	
	public void stop() {
		if (thread != null) {
			thread.interrupt();
		}
	}
	
	public int getTicksPerSecond() {
		return (int) (1000000000 / (double) deltaTime);
	}
	
	public double getDeltaTime() {
		return deltaTime;
	}
	
}
