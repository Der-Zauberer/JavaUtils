package eu.derzauberer.javautils.util;

public interface Command {
	
	boolean onCommand(Sender sender, String label, String args[]) throws Exception;

}
