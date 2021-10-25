package javautils.util;

public class EventProfile {

	private EventType type;
	private EventExecuter executer;
	
	public EventProfile(EventType type, EventExecuter executer) {
		this.type = type;
		this.executer = executer;
	}
	
	public EventType getType() {
		return type;
	}
	
	public EventExecuter getExecuter() {
		return executer;
	}
	
}
