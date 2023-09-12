package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Sender;

/**
 * This event gets called when an execution of a command fails.
 */
public class CommandExecutionFailedEvent extends CancellableEvent {

	/**
	 * Represents the reason of the execution fail.
	 */
	public enum ExecutionFailCause { EXCEPTION, BAD_RETURN }
	
	private final Sender sender;
	private final Command command;
	private final ExecutionFailCause cause;
	private final Exception exception;
	private final String input;
	private final String label;
	private final String[] args;
	
	/**
	 * Creates a new event that gets called when an execution of a command fails.
	 * 
	 * @param sender    the sender that called the command
	 * @param command   the command that failed to execute
	 * @param cause     the reason of the execution fail
	 * @param exception the exception, which is the reason of the execution fail
	 * @param input     the input that was used to execute the command
	 * @param label     the label of the command
	 * @param args      the arguments of the command
	 */
	public CommandExecutionFailedEvent(Sender sender, Command command, ExecutionFailCause cause, Exception exception, String input, String label, String[] args) {
		this.sender = sender;
		this.command = command;
		this.cause = cause;
		this.exception = exception;
		this.input = input;
		this.label = label;
		this.args = args;
	}
	
	/**
	 * Returns the sender that called the command.
	 * 
	 * @return the sender that called the command
	 */
	public Sender getSender() {
		return sender;
	}
	
	/**
	 * Returns the command that failed to execute.
	 * 
	 * @return the command that failed to execute
	 */
	public Command getCommand() {
		return command;
	}
	
	/**
	 * Returns the reason of the execution fail.
	 * 
	 * @return the reason of the execution fail
	 */
	public ExecutionFailCause getCause() {
		return cause;
	}
	
	/**
	 * Returns the exception, which is the reason of the execution fail.
	 * 
	 * @return the exception, which is the reason of the execution fail
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Returns the input that was used to execute the command.
	 * 
	 * @return the input that was used to execute the command
	 */
	public String getInput() {
		return input;
	}
	
	/**
	 * Returns the label of the command.
	 * 
	 * @return the label of the command
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Returns the arguments of the command.
	 * 
	 * @return the arguments of the command
	 */
	public String[] getArgs() {
		return args;
	}
	
}
