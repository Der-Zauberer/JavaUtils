package eu.derzauberer.javautils.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import eu.derzauberer.javautils.events.CommandExecutionFailedEvent;
import eu.derzauberer.javautils.events.CommandNotFoundEvent;
import eu.derzauberer.javautils.events.CommandPreProcessingEvent;
import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Console;
import eu.derzauberer.javautils.util.DataUtil;

public class CommandHandler {

	private HashMap<String, Command> commands = new HashMap<>();
	private ArrayList<String> history = new ArrayList<>();

	public void registerCommand(String label, Command command) {
		commands.put(label, command);
	}

	public boolean executeCommand(Console console, String string) {
		history.add(string);
		String command[] = getSplitedCommand(string);
		String label = command[0];
		String args[] = Arrays.copyOfRange(command, 1, command.length);
		return executeCommand(console, string, label, args);
	}

	private boolean executeCommand(Console console, String command, String label, String args[]) {
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