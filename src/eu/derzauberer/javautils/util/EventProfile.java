package eu.derzauberer.javautils.util;

public class EventProfile {

	private Class<? extends Event> type;
	private EventExecuter executer;
	
	public EventProfile(Class<? extends Event> type, EventExecuter executer) {
		this.type = type;
		this.executer = executer;
	}
	
	public Class<? extends Event> getType() {
		return type;
	}
	
	public EventExecuter getExecuter() {
		return executer;
	}
	
}
