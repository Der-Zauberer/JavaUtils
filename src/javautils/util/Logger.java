package javautils.util;

import java.util.ArrayList;

public class Logger {
	
	private static ArrayList<Console> consoles = new ArrayList<>();
	
	protected static void addConsole(Console console) {
		consoles.add(console);
	}
	
	protected static void removeConsole(Console console) {
		consoles.remove(console);
	}
	
	public static void sendMessage(Object object) {
		for (Console console : consoles) {
			console.sendMessage(object);
		}
	}

	public static void sendInfoMessage(Object object) {
		for (Console console : consoles) {
			console.sendInfoMessage(object);
		}
	}

	public static void sendWarningMessage(Object object) {
		for (Console console : consoles) {
			console.sendWarningMessage(object);
		}
	}

	public static void sendErrorMessage(Object object) {
		for (Console console : consoles) {
			console.sendErrorMessage(object);
		}
	}

}
