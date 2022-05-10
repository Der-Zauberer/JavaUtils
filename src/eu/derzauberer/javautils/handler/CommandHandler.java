package eu.derzauberer.javautils.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import eu.derzauberer.javautils.action.CommandExecutionFailedAction;
import eu.derzauberer.javautils.action.CommandNotFoundAction;
import eu.derzauberer.javautils.action.CommandPreProcessingAction;
import eu.derzauberer.javautils.events.CommandExecutionFailedEvent;
import eu.derzauberer.javautils.events.CommandExecutionFailedEvent.ExecutionFailCause;
import eu.derzauberer.javautils.events.CommandNotFoundEvent;
import eu.derzauberer.javautils.events.CommandPreProcessingEvent;
import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.DataUtil;
import eu.derzauberer.javautils.util.Sender;

public class CommandHandler {

	private HashMap<String, Command> commands = new HashMap<>();
	private ArrayList<String> history = new ArrayList<>();
	
	private CommandPreProcessingAction commandPreProcessingAction;
	private CommandExecutionFailedAction commandExecutionFailedAction;
	private CommandNotFoundAction commandNotFoundAction;
	
	public void registerCommand(String label, Command command) {
		commands.put(label, command);
	}

	public boolean executeCommand(Sender sender, String string) {
		history.add(string);
		String command[] = getSplitedCommand(string);
		if (command.length == 0) return false;
		String label = command[0];
		String args[] = Arrays.copyOfRange(command, 1, command.length);
		return executeCommand(sender, string, label, args);
	}

	private boolean executeCommand(Sender sender, String command, String label, String args[]) {
		for (String string : commands.keySet()) {
			if (string.equalsIgnoreCase(label)) {
				CommandPreProcessingEvent event = new CommandPreProcessingEvent(sender, commands.get(string), string, label, args);
				if (commandPreProcessingAction != null && !event.isCancelled()) commandPreProcessingAction.onAction(event);
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
						CommandExecutionFailedEvent commandExecutionFailedEvent = new CommandExecutionFailedEvent(event.getSender(), event.getCommand(), cause, exception, event.getString(), event.getLabel(), event.getArgs());
						if (commandExecutionFailedAction != null && !commandExecutionFailedEvent.isCancelled()) commandExecutionFailedAction.onAction(commandExecutionFailedEvent);
						if (!commandExecutionFailedEvent.isCancelled() && exception != null) exception.printStackTrace();
					}
					return success;
				} else break;
			}
		}
		CommandNotFoundEvent commandNotFoundEvent = new CommandNotFoundEvent(sender, command, label, args);
		if (commandNotFoundAction != null) commandNotFoundAction.onAction(commandNotFoundEvent);
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
		String string[] = new String[history.size()];
		for (int i = 0; i < history.size(); i++) {
			string[i] = history.get(i);
		}
		return string;
	}
	
	public void setOnCommandPreProcessing(CommandPreProcessingAction action) {
		commandPreProcessingAction = action;
	}
	
	public CommandPreProcessingAction getOnCommandPreProcessing() {
		return commandPreProcessingAction;
	}
	
	public void setOnCommandExecutionFailed(CommandExecutionFailedAction action) {
		commandExecutionFailedAction = action;
	}
	
	public CommandExecutionFailedAction getOnCommandExecutionFailed() {
		return commandExecutionFailedAction;
	}
	
	public void setOnCommandNotFound(CommandNotFoundAction action) {
		commandNotFoundAction = action;
	}
	
	public CommandNotFoundAction getOnCommandNotFound() {
		return commandNotFoundAction;
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
		ArrayList<String> strings = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
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
		String command[] = new String[strings.size()];
		for (int i = 0; i < command.length; i++) {
			command[i] = strings.get(i);
		}
		return command;
	}

}