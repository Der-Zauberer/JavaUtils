package javautils.handler;

import java.util.ArrayList;
import javautils.util.Event;
import javautils.util.EventExecuter;
import javautils.util.EventProfile;
import javautils.util.EventType;

public class EventHandler {

	private static ArrayList<EventProfile> events = new ArrayList<>();

	public static void registerEvent(EventType type, EventExecuter executer) {
		events.add(new EventProfile(type, executer));
	}
	
	public static void executeEvent(EventType type, Event event) {
		for (EventProfile profile : events) {
			if (profile.getType() == type) {
				profile.getExecuter().executeEvent(event);
			}
		}
	}

}