package eu.derzauberer.javautils.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import eu.derzauberer.javautils.events.CommandExecutionFailedEvent;
import eu.derzauberer.javautils.events.CommandExecutionFailedEvent.ExecutionFailCause;
import eu.derzauberer.javautils.events.CommandNotFoundEvent;
import eu.derzauberer.javautils.events.CommandPreProcessingEvent;
import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.DataUtil;
import eu.derzauberer.javautils.util.Sender;

public class CommandController {

	private final HashMap<String, Command> commands = new HashMap<>();
	private final ArrayList<String> history = new ArrayList<>();
	
	private Consumer<CommandPreProcessingEvent> preProcessingAction;
	private Consumer<CommandExecutionFailedEvent> executionFailedAction;
	private Consumer<CommandNotFoundEvent> notFoundAction;
	
	public void registerCommand(String label, Command command) {
		commands.put(label, command);
	}

	public boolean executeCommand(Sender sender, String string) {
		history.add(string);
		final String command[] = getSplitedCommand(string);
		if (command.length == 0) return false;
		final String label = command[0];
		final String args[] = Arrays.copyOfRange(command, 1, command.length);
		return executeCommand(sender, string, label, args);
	}

	private boolean executeCommand(Sender sender, String command, String label, String args[]) {
		for (String string : commands.keySet()) {
			if (string.equalsIgnoreCase(label)) {
				final CommandPreProcessingEvent event = new CommandPreProcessingEvent(sender, commands.get(string), string, label, args);
				if (preProcessingAction != null && !event.isCancelled()) preProcessingAction.accept(event);
				if (!event.isCancelled()) {
					boolean success;
					ExecutionFailCause cause = ExecutionFailCause.BAD_RETURN;
					Exception exception = null;
					try {
						success = commands.get(label).onCommand(event.getSender(), event.getLabel(), event.getArgs());
					} catch (Exception e) {
						exception = e;
						success = false;
						cause = ExecutionFailCause.EXCEPTION;
					}
					if (!success) {
						final CommandExecutionFailedEvent commandExecutionFailedEvent = new CommandExecutionFailedEvent(event.getSender(), event.getCommand(), cause, exception, event.getString(), event.getLabel(), event.getArgs());
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

	public HashMap<String, Command> getCommands() {
		return commands;
	}

	public Command getCommand(String string) {
		if (commands.containsKey(string)) return commands.get(string);
		return null;
	}

	public String[] getHistory() {
		final String string[] = new String[history.size()];
		for (int i = 0; i < history.size(); i++) {
			string[i] = history.get(i);
		}
		return string;
	}
	
	public void setPreProcessingAction(Consumer<CommandPreProcessingEvent> preProcessingAction) {
		this.preProcessingAction = preProcessingAction;
	}
	
	public Consumer<CommandPreProcessingEvent> getPreProcessingAction() {
		return preProcessingAction;
	}
	
	public void setExecutionFailedAction(Consumer<CommandExecutionFailedEvent> executionFailedAction) {
		this.executionFailedAction = executionFailedAction;
	}
	
	public Consumer<CommandExecutionFailedEvent> getExecutionFailedAction() {
		return executionFailedAction;
	}
	
	public void setNotFoundAction(Consumer<CommandNotFoundEvent> notFoundAction) {
		this.notFoundAction = notFoundAction;
	}
	
	public Consumer<CommandNotFoundEvent> getNotFoundAction() {
		return notFoundAction;
	}
	
	public static boolean getCondition(String args[], String condition, int position) {
		return args.length - 1 >= position && args[position] != null && args[position].equalsIgnoreCase(condition);
	}

	public static boolean hasCondition(String args[], String condition) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase(condition)) return true;
		}
		return false;
	}

	private static String[] getSplitedCommand(String string) {
		final ArrayList<String> strings = new ArrayList<>();
		final StringBuilder builder = new StringBuilder();
		char lastCharacter = ' ';
		boolean enclosed = false;
		for (char character : string.toCharArray()) {
			if (character == ' ' && !enclosed) {
				if (builder.length() != 0) {
					strings.add(DataUtil.addEscapeCodes(builder.toString()));
					builder.setLength(0);
				}
			} else if (character == '"' && lastCharacter != '\\') {
				if (!enclosed) {
					enclosed = true;
				} else {
					strings.add(DataUtil.addEscapeCodes(builder.toString()));
					builder.setLength(0);
					enclosed = false;
				}
			} else {
				builder.append(character);
			}
			lastCharacter = character;
		}
		if (builder.length() != 0) strings.add(DataUtil.addEscapeCodes(builder.toString()));
		final String command[] = new String[strings.size()];
		for (int i = 0; i < command.length; i++) {
			command[i] = strings.get(i);
		}
		return command;
	}

}