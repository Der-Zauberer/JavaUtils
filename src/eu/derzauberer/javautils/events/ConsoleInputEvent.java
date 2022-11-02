package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.controller.CommandController;
import eu.derzauberer.javautils.controller.ConsoleController;

/**
 * This event gets called when an input was given to the
 * {@link CommandController}.
 */
public class ConsoleInputEvent extends CancellableEvent {
	
	private final ConsoleController console;
	private String input;
	
	/**
	 * Creates a new event that gets called when an input was given to the
	 * {@link ConsoleController}.
	 * 
	 * @param console the console receiving the input
	 * @param input   the input that was given from the console
	 */
	public ConsoleInputEvent(ConsoleController console, String input) {
		this.console = console;
		this.input = input;
	}

	/**
	 * Returns the console receiving the input.
	 * 
	 * @return the console receiving the input
	 */
	public ConsoleController getConsole() {
		return console;
	}

	/**
	 * Sets input that was given from the console.
	 * 
	 * @param input that was given from the console
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * Returns input that was given from the console.
	 * 
	 * @return input that was given from the console
	 */
	public String getInput() {
		return input;
	}

}
