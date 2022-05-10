package eu.derzauberer.javautils.util;

public interface Command {
	
	public abstract boolean onCommand(Sender sender, String label, String args[]) throws Exception;

}
