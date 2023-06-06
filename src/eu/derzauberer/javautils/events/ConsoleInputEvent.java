package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.service.ConsoleService;

/**
 * This event gets called when an input was given to the
 * {@link CommandService}.
 */
public class ConsoleInputEvent extends CancellableEvent {
	
	private final ConsoleService console;
	private String input;
	
	/**
	 * Creates a new event that gets called when an input was given to the
	 * {@link ConsoleControllerOld}.
	 * 
	 * @param console the console receiving the input
	 * @param input   the input that was given from the console
	 */
	public ConsoleInputEvent(ConsoleService console, String input) {
		this.console = console;
		this.input = input;
	}

	/**
	 * Returns the console receiving the input.
	 * 
	 * @return the console receiving the input
	 */
	public ConsoleService getConsole() {
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
