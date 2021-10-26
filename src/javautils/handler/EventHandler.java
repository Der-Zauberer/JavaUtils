package javautils.handler;

import java.util.ArrayList;
import javautils.util.Event;
import javautils.util.EventExecuter;
import javautils.util.EventProfile;

@SuppressWarnings("rawtypes")
public class EventHandler {

	private static ArrayList<EventProfile> events = new ArrayList<>();

	public static void registerEvent(Class type, EventExecuter executer) {
		events.add(new EventProfile(type, executer));
	}
	
	public static void executeEvent(Class type, Event event) {
		for (EventProfile profile : events) {
			if (profile.getType() == type) {
				profile.getExecuter().executeEvent(event);
			}
		}
	}

}