package eu.derzauberer.javautils.handler;

import java.util.ArrayList;

import eu.derzauberer.javautils.util.Event;
import eu.derzauberer.javautils.util.EventExecuter;
import eu.derzauberer.javautils.util.EventProfile;

public class EventHandler {

	private static ArrayList<EventProfile> events = new ArrayList<>();

	public static void registerEvent(Class<? extends Event> type, EventExecuter executer) {
		events.add(new EventProfile(type, executer));
	}
	
	public static void executeEvent(Class<? extends Event> type, Event event) {
		for (EventProfile profile : events) {
			if (profile.getType() == type) {
				profile.getExecuter().executeEvent(event);
			}
		}
	}

}