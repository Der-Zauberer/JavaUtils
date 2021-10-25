package javauitls.events;

import java.io.File;

import javautils.util.Console;
import javautils.util.Event;

public class CommandNotFoundEvent extends Event {
	
	private Console console;
	private String command;
	private String label;
	private String args[];
	private File directory;
	
	public CommandNotFoundEvent(Console console, String command, String label, String args[], File directory) {
		this.console = console;
		this.command = command;
		this.label = label;
		this.args = args;
		this.directory = directory;
	}
	
	public Console getConsole() {
		return console;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public File getDirectory() {
		return directory;
	}

}
