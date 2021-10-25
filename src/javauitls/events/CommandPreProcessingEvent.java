package javauitls.events;

import java.io.File;

import javautils.util.Console;
import javautils.util.Event;

public class CommandPreProcessingEvent extends Event {

	private boolean cancled;
	private Console console;
	private String command;
	private String label;
	private String args[];
	private File directory;
	
	public CommandPreProcessingEvent(Console console, String command, String label, String args[], File directory) {
		this.cancled = false;
		this.console = console;
		this.command = command;
		this.label = label;
		this.args = args;
		this.directory = directory;
	}
	
	public void setCancled(boolean cancled) {
		this.cancled = cancled;
	}
	
	public boolean isCancled() {
		return cancled;
	}
	
	public void setConsole(Console console) {
		this.console = console;
	}
	
	public Console getConsole() {
		return console;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setArgs(String[] args) {
		this.args = args;
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public void setDirectory(File directory) {
		this.directory = directory;
	}
	
	public File getDirectory() {
		return directory;
	}
	
}
