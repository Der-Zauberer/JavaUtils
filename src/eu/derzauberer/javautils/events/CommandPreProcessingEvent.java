package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Sender;

/**
 * This event gets called before the execution of a command.
 */
public class CommandPreProcessingEvent extends CancellableEvent {

	private final Sender sender;
	private final Command command;
	private final String input;
	private String label;
	private String args[];
	
	/**
	 * Creates a new event that gets calle before the execution of a command.
	 * 
	 * @param sender  the sender that called the command
	 * @param command the command to execute
	 * @param input   the input that was used to execute the command
	 * @param label   the label of the command
	 * @param args    the arguments of the command
	 */
	public CommandPreProcessingEvent(Sender sender, Command command, String input, String label, String args[]) {
		this.sender = sender;
		this.command = command;
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
	 * Returns the command to execute.
	 * 
	 * @return the command to execute
	 */
	public Command getCommand() {
		return command;
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
	 * Sets the label the label of the command.
	 * 
	 * @param label the label the label of the command
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Returns the label the label of the command.
	 * 
	 * @return the label the label of the command
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Sets the arguments of the command.
	 * 
	 * @param args the arguments of the command
	 */
	public void setArgs(String[] args) {
		this.args = args;
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
