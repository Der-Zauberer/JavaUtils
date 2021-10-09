package javautils.util;

import java.util.ArrayList;
import java.util.Scanner;
import javautils.handler.CommandHandler;

public class Console {

	private static ArrayList<String> history = new ArrayList<>();
	private static Thread thread;
	private static String prefix;

	public static void startConsole() {
		startConsole("");
	}
	
	@SuppressWarnings("resource")
	public static void startConsole(String prefix) {
		Console.prefix = prefix;
		thread = new Thread(() -> {
			Scanner scanner = new Scanner(System.in);
			while (true) {
				System.out.print(prefix + "~ ");
				CommandHandler.executeCommand(scanner.nextLine());
			}
		});
		thread.start();
	}

	public void stopConsole() {
		thread.interrupt();
	}

	public static void setPrefix(String prefix) {
		Console.prefix = prefix;
	}

	public static String getPrefix() {
		return prefix;
	}
	
	public static void sendMessage(Object object) {
		history.add(object.toString());
		System.out.println(object);
	}

	public static void sendInfoMessage(Object object) {
		history.add(object.toString());
		System.out.println("[INFO] " + object);
	}

	public static void sendWarningMessage(Object object) {
		history.add(object.toString());
		System.out.println("[WARNING] " + object);
	}

	public static void sendErrorMessage(Object object) {
		history.add(object.toString());
		System.out.println("[ERROR] " + object);
	}
	
	public static String[] getHistory() {
		String string[] = new String[history.size()];
		for (int i = 0; i < history.size(); i++) {
			string[i] = history.get(i);
		}
		return string;
	}

}
