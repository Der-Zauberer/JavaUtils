package javautils.util;

import java.io.File;

public interface Command {
	
	public abstract boolean onCommand(Console console, String label, String args[], File directoy);
	public abstract String getCommandHelp();

}
