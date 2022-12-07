package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Sender;

/**
 * This event gets called when the command couldn't be found to execute.
 */
public class CommandNotFoundEvent extends Event {
	
	private final Sender sender;
	private final String input;
	private final String label;
	private final String[] args;
	
	/**
	 * Creates a new event that gets called when the command couldn't be found to
	 * execute.
	 * 
	 * @param sender the sender that called the command
	 * @param input  the input that was used to execute the command
	 * @param label  the label of the command
	 * @param args   the arguments of the command
	 */
	public CommandNotFoundEvent(Sender sender, String input, String label, String[] args) {
		this.sender = sender;
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
