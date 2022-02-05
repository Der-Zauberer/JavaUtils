package eu.derzauberer.javautils.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import eu.derzauberer.javautils.events.CommandExecutionFailedEvent;
import eu.derzauberer.javautils.events.CommandNotFoundEvent;
import eu.derzauberer.javautils.events.CommandPreProcessingEvent;
import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Console;

public class CommandHandler {

	private static HashMap<String, Command> commands = new HashMap<>();
	private static ArrayList<String> history = new ArrayList<>();

	public static void registerCommand(String label, Command command) {
		commands.put(label, command);
	}

	public static boolean executeCommand(Console console, String string) {
		history.add(string);
		String command[] = getSplitedCommand(string);
		String label = command[0];
		String args[] = Arrays.copyOfRange(command, 1, command.length);
		return executeCommand(console, string, label, args);
	}

	private static boolean executeCommand(Console console, String command, String label, String args[]) {
		for (String string : commands.keySet()) {
			if (string.equalsIgnoreCase(label)) {
				if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
					console.sendMessage(commands.get(string).getCommandHelp());
					return true;
				}
				CommandPreProcessingEvent event = new CommandPreProcessingEvent(console, commands.get(string), string, label, args);
				if (!event.isCancelled()) {
					boolean success = commands.get(label).onCommand(event.getConsole(), event.getLabel(), event.getArgs());
					if (!success) {
						new CommandExecutionFailedEvent(event.getConsole(), event.getCommand(), event.getString(), event.getLabel(), event.getArgs());
					}
					return success;
				} else {
					new CommandNotFoundEvent(console, command, label, args);
					return false;
				}
			}
		}
		new CommandNotFoundEvent(console, command, label, args);
		return false;
	}

	public static HashMap<String, Command> getCommands() {
		return commands;
	}

	public static Command getCommand(String string) {
		if (commands.containsKey(string)) {
			return commands.get(string);
		}
		return null;
	}

	public static boolean getCondition(String args[], String condition, int position) {
		if (args.length - 1 >= position && args[position] != null && args[position].equalsIgnoreCase(condition))
			return true;
		return false;
	}

	public static boolean hasCondition(String args[], String condition) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase(condition))
				return true;
		}
		return false;
	}

	public static String[] getHistory() {
		String string[] = new String[history.size()];
		for (int i = 0; i < history.size(); i++) {
			string[i] = history.get(i);
		}
		return string;
	}

	private static String[] getSplitedCommand(String string) {
		if (string == null || string.isEmpty()) {
			String list[] = { "" };
			return list;
		}
		ArrayList<String> strings = new ArrayList<>();
		string += " ";
		int lastSpace = -1;
		char lastChar = ' ';
		char character = ' ';
		boolean enclosed = false;
		for (int i = 0; i < string.length(); i++) {
			character = string.charAt(i);
			if (character == ' ' && !enclosed) {
				if (lastChar != ' ' && lastChar != '"') {
					strings.add(string.substring(lastSpace + 1, i));
				}
				lastSpace = i;
			} else if (character == '"' && !enclosed) {
				lastSpace = i;
				enclosed = true;
			} else if (character == '"' && enclosed) {
				strings.add(string.substring(lastSpace + 1, i));
				lastSpace = i;
				enclosed = false;
			}
			lastChar = character;
		}
		String command[] = new String[strings.size()];
		for (int i = 0; i < command.length; i++) {
			command[i] = strings.get(i);
		}
		return command;
	}

}