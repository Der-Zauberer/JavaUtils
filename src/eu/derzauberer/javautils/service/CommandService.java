package eu.derzauberer.javautils.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import eu.derzauberer.javautils.events.CommandExecutionFailedEvent;
import eu.derzauberer.javautils.events.CommandExecutionFailedEvent.ExecutionFailCause;
import eu.derzauberer.javautils.events.CommandNotFoundEvent;
import eu.derzauberer.javautils.events.CommandPreProcessingEvent;
import eu.derzauberer.javautils.parser.Parser;
import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Sender;

/**
 * This controller can process commands based on the given inputs.
 */
public class CommandService {

	private final Map<String, Command> commands = new HashMap<>();
	private final List<String> history = new ArrayList<>();
	
	private Consumer<CommandPreProcessingEvent> preProcessingAction;
	private Consumer<CommandExecutionFailedEvent> executionFailedAction;
	private Consumer<CommandNotFoundEvent> notFoundAction;
	
	/**
	 * Register a new command with its label.
	 * 
	 * @param label the name of the command
	 * @param command the command interface to execute when called
	 */
	public void registerCommand(String label, Command command) {
		commands.put(label, command);
	}

	/**
	 * Execute a command based on a given {@link String}.
	 * 
	 * @param sender the sender, which got the input for the command
	 * @param string the command as string input
	 * @return if the execution of the command was successful
	 */
	public boolean executeCommand(Sender sender, String string) {
		history.add(string);
		final String[] command = getSplitCommand(string);
		if (command.length == 0) return false;
		final String label = command[0];
		final String[] args = Arrays.copyOfRange(command, 1, command.length);
		return executeCommand(sender, string, label, args);
	}

	/**
	 * Execute a command based on the label and the arguments. Arguments can be an
	 * empty string array.
	 * 
	 * <pre>
	 * test arg1 args2 args3
	 * ^^^^ ^^^^^^^^^^^^^^^^
	 * label arguments
	 * </pre>
	 * 
	 * @param sender  the sender, which got the input for the command
	 * @param command the command interface to execute
	 * @param label   the name of the command
	 * @param args    the argument of the command
	 * @return if the execution of the command was successful
	 */
	private boolean executeCommand(Sender sender, String command, String label, String[] args) {
		for (String string : commands.keySet()) {
			if (string.equalsIgnoreCase(label)) {
				final CommandPreProcessingEvent event = new CommandPreProcessingEvent(sender, commands.get(string), string, label, args);
				if (preProcessingAction != null && !event.isCancelled()) preProcessingAction.accept(event);
				if (!event.isCancelled()) {
					boolean success;
					ExecutionFailCause cause = ExecutionFailCause.BAD_RETURN;
					Exception exception = null;
					try {
						success = commands.get(label).executeCommand(event.getSender(), event.getLabel(), event.getArgs());
					} catch (Exception e) {
						exception = e;
						success = false;
						cause = ExecutionFailCause.EXCEPTION;
					}
					if (!success) {
						final CommandExecutionFailedEvent commandExecutionFailedEvent = new CommandExecutionFailedEvent(event.getSender(), event.getCommand(), cause, exception, event.getInput(), event.getLabel(), event.getArgs());
						if (executionFailedAction != null && !commandExecutionFailedEvent.isCancelled()) executionFailedAction.accept(commandExecutionFailedEvent);
						if (!commandExecutionFailedEvent.isCancelled() && exception != null) exception.printStackTrace();
					}
					return success;
				} else break;
			}
		}
		final CommandNotFoundEvent commandNotFoundEvent = new CommandNotFoundEvent(sender, command, label, args);
		if (notFoundAction != null) notFoundAction.accept(commandNotFoundEvent);
		return false;
	}

	/**
	 * Returns an unmodifiable map of the commands based on their names.
	 * 
	 * @return an unmodifiable map of the commands based on their names
	 */
	public Map<String, Command> getCommands() {
		return Collections.unmodifiableMap(commands);
	}

	/**
	 * Returns the command based on its name.
	 * 
	 * @param label the name of the command
	 * @return the command by its name and null if not found
	 */
	public Command getCommand(String label) {
		return commands.get(label);
	}

	/**
	 * Returns an unmodifiable list of the last inputs.
	 * 
	 * @return an unmodifiable list of the last inputs
	 */
	public List<String> getInputHistory() {
		return Collections.unmodifiableList(history);
	}

	/**
	 * Sets an action to execute before the command gets executed.
	 * 
	 * @param preProcessingAction an action to execute when before the command gets
	 *                            executed
	 */
	public void setPreProcessingAction(Consumer<CommandPreProcessingEvent> preProcessingAction) {
		this.preProcessingAction = preProcessingAction;
	}

	/**
	 * Returns an action to execute before the command gets executed.
	 * 
	 * @return an action to execute before the command gets executed
	 */
	public Consumer<CommandPreProcessingEvent> getPreProcessingAction() {
		return preProcessingAction;
	}

	/**
	 * Sets an action to execute when the command execution failed.
	 * 
	 * @param executionFailedAction an action to execute when the command execution
	 *                              failed
	 */
	public void setExecutionFailedAction(Consumer<CommandExecutionFailedEvent> executionFailedAction) {
		this.executionFailedAction = executionFailedAction;
	}

	/**
	 * Returns an action to execute when the command execution failed.
	 * 
	 * @return an action to execute when the command execution failed
	 */
	public Consumer<CommandExecutionFailedEvent> getExecutionFailedAction() {
		return executionFailedAction;
	}

	/**
	 * Sets an action to execute when no command was found for execution.
	 * 
	 * @param notFoundAction an action to execute when no command was found for
	 *                       execution
	 */
	public void setNotFoundAction(Consumer<CommandNotFoundEvent> notFoundAction) {
		this.notFoundAction = notFoundAction;
	}

	/**
	 * Returns an action to execute when no command was found for execution.
	 * 
	 * @return an action to execute when no command was found for execution
	 */
	public Consumer<CommandNotFoundEvent> getNotFoundAction() {
		return notFoundAction;
	}

	/**
	 * Splits the string in words except for words in quotation marks and returns
	 * them as array.
	 * 
	 * <pre>
	 * test test test    ->   ["test", "test", "test"]
	 * 
	 * "test test" test "test \"test\""    ->   ["test test", "test", "test \"test\""]
	 * </pre>
	 * 
	 * @param string the input string
	 * @return the split string as array
	 */
	private static String[] getSplitCommand(String string) {
		final ArrayList<String> strings = new ArrayList<>();
		final StringBuilder builder = new StringBuilder();
		char lastCharacter = ' ';
		boolean enclosed = false;
		for (char character : string.toCharArray()) {
			if (character == ' ' && !enclosed) {
				if (builder.length() != 0) {
					strings.add(Parser.addEscapeCodes(builder.toString()));
					builder.setLength(0);
				}
			} else if (character == '"' && lastCharacter != '\\') {
				if (!enclosed) {
					enclosed = true;
				} else {
					strings.add(Parser.addEscapeCodes(builder.toString()));
					builder.setLength(0);
					enclosed = false;
				}
			} else {
				builder.append(character);
			}
			lastCharacter = character;
		}
		if (builder.length() != 0) strings.add(Parser.addEscapeCodes(builder.toString()));
		final String[] command = new String[strings.size()];
		for (int i = 0; i < command.length; i++) {
			command[i] = strings.get(i);
		}
		return command;
	}

}