package eu.derzauberer.javautils.util;

import eu.derzauberer.javautils.handler.ConsoleHandler;

public interface Command {
	
	public abstract boolean onCommand(ConsoleHandler console, String label, String args[]);
	public abstract String getCommandHelp();

}
