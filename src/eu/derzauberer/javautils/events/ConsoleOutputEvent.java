package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.controller.CommandController;
import eu.derzauberer.javautils.controller.ConsoleController;
import eu.derzauberer.javautils.util.Sender.MessageType;

/**
 * This event gets called when the
 * {@link CommandController} sends an output.
 */
public class ConsoleOutputEvent extends CancellableEvent {
	
	private final ConsoleController console;
	private String output;
	private final MessageType type;
	
	/**
	 * Creates a new event that gets called when the {@link CommandController} sends
	 * an output.
	 * 
	 * @param console the console sending the output
	 * @param output  the output, which the console will print
	 * @param type    the type of the message
	 */
	public ConsoleOutputEvent(ConsoleController console, String output, MessageType type) {
		this.console = console;
		this.output = output;
		this.type = type;
	}

	/**
	 * Returns the console sending the output.
	 * 
	 * @return the console sending the output
	 */
	public ConsoleController getConsole() {
		return console;
	}
	
	/**
	 * Sets the output, which the console will print.
	 * 
	 * @param output the output, which the console will print
	 */
	public void setOutput(String output) {
		this.output = output;
	}
	
	/**
	 * Returns the output, which the console will print.
	 * 
	 * @return output the output, which the console will print
	 */
	public String getOutput() {
		return output;
	}
	
	/**
	 * Returns which type the message should be.
	 * 
	 * @return which type the message should be
	 */
	public MessageType getType() {
		return type;
	}

}
