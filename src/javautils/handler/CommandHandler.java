package javautils.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javautils.util.Command;

public class CommandHandler {

	private static HashMap<String, Command> commands = new HashMap<>();
	private static ArrayList<String> history = new ArrayList<>();

	public static void registerCommand(String label, Command command) {
		commands.put(label, command);
	}

	public static boolean executeCommand(String string) {
		history.add(string);
		String command[] = getSplitedCommand(string);
		String label = command[0];
		String args[] = Arrays.copyOfRange(command, 1, command.length);
		return executeCommand(label, args);
	}

	private static boolean executeCommand(String label, String args[]) {
		for (String string : commands.keySet()) {
			if (string.equalsIgnoreCase(label)) {
				if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
					sendMessageOutput(commands.get(string).getCommandHelp());
					return true;
				}
				return commands.get(string).onCommand(string, args, FileHandler.getJarDirectory());
			}
		}
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

	public static ArrayList<String> getHistory() {
		return history;
	}

	public static void sendMessageOutput(Object object) {
		System.out.println(object);
	}

	public static void sendInfoOutput(Object object) {
		System.out.println("[INFO] " + object);
	}

	public static void sendWarningOutput(Object object) {
		System.out.println("[WARNING] " + object);
	}

	public static void sendErrorOutput(Object object) {
		System.out.println("[ERROR] " + object);
	}

	private static String[] getSplitedCommand(String string) {
		if (string == null || string.isBlank()) {
			String list[] = { "" };
			return list;
		}
		ArrayList<String> strings = new ArrayList<>();
		string += " ";
		int lastspace = -1;
		char lastchar = ' ';
		char character = ' ';
		boolean enclosed = false;
		for (int i = 0; i < string.length(); i++) {
			character = string.charAt(i);
			if (character == ' ' && !enclosed) {
				if (lastchar != ' ' && lastchar != '"') {
					strings.add(string.substring(lastspace + 1, i));
				}
				lastspace = i;
			} else if (character == '"' && !enclosed) {
				lastspace = i;
				enclosed = true;
			} else if (character == '"' && enclosed) {
				strings.add(string.substring(lastspace + 1, i));
				lastspace = i;
				enclosed = false;
			}
			lastchar = character;
		}
		String command[] = new String[strings.size()];
		for (int i = 0; i < command.length; i++) {
			command[i] = strings.get(i);
		}
		return command;
	}

}
