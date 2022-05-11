package eu.derzauberer.javautils.util;

import java.lang.management.ManagementFactory;
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
	
	public abstract void sendInput(String string);
	public abstract void sendOutput(String string, MessageType type);
	
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
			sendOutput(output, type);
		}
	}
	
	public default void sendMessage(MessageType type, String string, String... args) {
		String output = string;
		for (int i = 0; i < args.length && output.contains("{}"); i++) {
			output = output.replaceFirst(Pattern.quote("{}"), args[i]);
		}
		sendMessage(type, output);
	}
	
	public default boolean isDebugEnabled() {
		return ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}
	
	public abstract MessageType getDefaultMessageType();

}
