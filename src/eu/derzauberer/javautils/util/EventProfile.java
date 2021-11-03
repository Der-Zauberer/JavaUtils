package eu.derzauberer.javautils.util;

@SuppressWarnings("rawtypes")
public class EventProfile {

	private Class type;
	private EventExecuter executer;
	
	public EventProfile(Class type, EventExecuter executer) {
		this.type = type;
		this.executer = executer;
	}
	
	public Class getType() {
		return type;
	}
	
	public EventExecuter getExecuter() {
		return executer;
	}
	
}
