package javautils.util;

import java.util.Scanner;
import javautils.handler.CommandHandler;

public class Console {
	
	private static Thread thread;
	private static String prefix = "";
	
	@SuppressWarnings("resource")
	public Console() {
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
	
	public void startConsole() {
		thread.start();
	}
	
	public static void setPrefix(String prefix) {
		Console.prefix = prefix;
	}
	
	public static String getPrefix() {
		return prefix;
	}

}
