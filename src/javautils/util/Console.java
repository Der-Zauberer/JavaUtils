package javautils.util;

import java.util.Scanner;
import javautils.handler.CommandHandler;

public class Console {

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
		System.out.println(object);
	}

	public static void sendInfoMessage(Object object) {
		System.out.println("[INFO] " + object);
	}

	public static void sendWarningMessage(Object object) {
		System.out.println("[WARNING] " + object);
	}

	public static void sendErrorMessage(Object object) {
		System.out.println("[ERROR] " + object);
	}

}
