package javautils.util;

public interface Command {
	
	public abstract boolean onCommand(Console console, String label, String args[]);
	public abstract String getCommandHelp();

}
