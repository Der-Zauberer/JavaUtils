package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Sender;

public class CommandExecutionFailedEvent extends CancellableEvent {

	public enum ExecutionFailCause {EXCEPTION, BAD_RETURN}
	
	private final Sender sender;
	private final Command command;
	private final ExecutionFailCause cause;
	private final Exception exception;
	private final String string;
	private final String label;
	private final String args[];
	
	public CommandExecutionFailedEvent(Sender sender, Command command, ExecutionFailCause cause, Exception exception, String string, String label, String args[]) {
		this.sender = sender;
		this.command = command;
		this.cause = cause;
		this.exception = exception;
		this.string = string;
		this.label = label;
		this.args = args;
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
	
}
