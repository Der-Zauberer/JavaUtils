package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Sender;

public class CommandExecutionFailedEvent extends Event {

	public enum ExecutionFailCause {
		EXCEPTION,
		BAD_RETURN
	}
	
	private Sender sender;
	private Command command;
	private ExecutionFailCause cause;
	private Exception exception;
	private String string;
	private String label;
	private String args[];
	
	public CommandExecutionFailedEvent(Sender sender, Command command, ExecutionFailCause cause, Exception exception, String string, String label, String args[]) {
		this.sender = sender;
		this.command = command;
		this.cause = cause;
		this.exception = exception;
		this.string = string;
		this.label = label;
		this.args = args;
		execute();
	}
	
	public Sender getSender() {
		return sender;
	}
	
	public Command getCommand() {
		return command;
	}
	
	public ExecutionFailCause getCause() {
		return cause;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public String getString() {
		return string;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}
	
}
