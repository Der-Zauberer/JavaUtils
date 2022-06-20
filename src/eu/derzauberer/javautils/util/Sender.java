package eu.derzauberer.javautils.util;

import java.lang.management.ManagementFactory;
import java.util.regex.Pattern;

public interface Sender {
	
	enum MessageType {DEFAULT, DEBUG, INFO, SUCCESS, WARNING, ERROR}
	
	void sendInput(String input);
	void sendOutput(String message, MessageType type);
	
	default void sendMessage(String message) {
		sendMessage(getDefaultMessageType(), message);
	}
	
	default void sendMessage(String message, String... args) {
		sendMessage(getDefaultMessageType(), message, args);
	}
	
	default void sendMessage(MessageType type, String message) {
		if (type != MessageType.DEBUG || isDebugEnabled()) {
			String output;
			if (type == MessageType.DEFAULT) output = message;
			else output = "[" + type.toString() + "] " + message;
			sendOutput(output, type);
		}
	}
	
	default void sendMessage(MessageType type, String message, String... args) {
		String output = message;
		for (int i = 0; i < args.length && output.contains("{}"); i++) {
			output = output.replaceFirst(Pattern.quote("{}"), args[i]);
		}
		sendMessage(type, output);
	}
	
	default boolean isDebugEnabled() {
		return ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}
	
	void setDefaultMessageType(MessageType type);
	
	MessageType getDefaultMessageType();

}
