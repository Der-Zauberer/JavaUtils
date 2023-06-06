package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.service.ConsoleService;

/**
 * This event gets called when the
 * {@link CommandService} sends an output.
 */
public class ConsoleOutputEvent extends CancellableEvent {
	
	private final ConsoleService console;
	private String output;
	
	/**
	 * Creates a new event that gets called when the {@link CommandService} sends
	 * an output.
	 * 
	 * @param console the console sending the output
	 * @param output  the output, which the console will print
	 */
	public ConsoleOutputEvent(ConsoleService console, String output) {
		this.console = console;
		this.output = output;
	}

	/**
	 * Returns the console sending the output.
	 * 
	 * @return the console sending the output
	 */
	public ConsoleService getConsole() {
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

}
