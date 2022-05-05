package eu.derzauberer.javautils.util;

import java.util.regex.Pattern;

public interface Sender {
	
	public enum MessageType {
		DEFAULT,
		DEBUG,
		INFO,
		SUCCESS,
		WARNING,
		ERROR,
	}
	
	public default void sendInput(String string) {
		onInput(string);
	}
	
	public abstract void onInput(String string);
	
	public default void sendMessage(String string) {
		sendMessage(getDefaultMessageType(), string);
	}
	
	public default void sendMessage(String string, String... args) {
		sendMessage(getDefaultMessageType(), string, args);
	}
	
	public default void sendMessage(MessageType type, String string) {
		if (type != MessageType.DEBUG || isDebugEnabled()) {
			String output;
			if (type == MessageType.DEFAULT) output = string;
			else output = "[" + type.toString() + "] " + string;
			onOutput(output);
		}
	}
	
	public default void sendMessage(MessageType type, String string, String... args) {
		String output = string;
		for (int i = 0; i < args.length && output.contains("{}"); i++) {
			output = output.replaceFirst(Pattern.quote("{}"), args[i]);
		}
		sendMessage(type, output);
	}
	
	public abstract void onOutput(String string);
	
	public abstract MessageType getDefaultMessageType();
	public abstract boolean isDebugEnabled();

}
