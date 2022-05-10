package eu.derzauberer.javautils.handler;

public class LoopHandler {
	
	private Thread thread;
	private Runnable runnable;
	private boolean isRunning;
	private long deltaTimeNanos;
	private long lastTimestamp;
	
	public LoopHandler(Runnable runnable) {
		this.runnable = runnable;
		isRunning = false;
		deltaTimeNanos = 0;
		lastTimestamp = 0;
	}
	
	private void loop() {
		while (!thread.isInterrupted()) {
			long time = System.nanoTime();
			deltaTimeNanos = time - lastTimestamp;
			lastTimestamp = time;
			runnable.run();
		}
	}
	
	public void start() {
		if (!isRunning) {
			isRunning = true;
			thread = new Thread(this::loop);
			thread.start();
		}
	}
	
	public void stop() {
		if (isRunning) {
			isRunning = false;
			thread.interrupt();
		}
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public int getTicksPerSecond() {
		return (int) (1000000000 / (double) deltaTimeNanos);
	}
	
	public long getDeltaTimeNanos() {
		return deltaTimeNanos;
	}
	
	public long getDeltaTimeMycros() {
		return deltaTimeNanos / 1000;
	}
	
	public long getDeltaTimeMillis() {
		return deltaTimeNanos / 1000000;
	}
	
}
