package eu.derzauberer.javautils.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import eu.derzauberer.javautils.annotations.EventListener;
import eu.derzauberer.javautils.events.Event;
import eu.derzauberer.javautils.util.Listener;

public class EventHandler {

	private static final HashMap<Method, Listener> methods = new HashMap<>();
	
	public static void registerEvents(Listener listener) {
		for (Method method : listener.getClass().getDeclaredMethods()) {
			if (method.getAnnotation(EventListener.class) != null) {
				method.setAccessible(true);
				methods.put(method, listener);
			}
		}
	}
	
	public static void executeEvent(Event event) {
		final ArrayList<Method> highestPriority = new ArrayList<>();
		final ArrayList<Method> hightPriority = new ArrayList<>();
		final ArrayList<Method> normalPriority = new ArrayList<>();
		final ArrayList<Method> lowPriority = new ArrayList<>();
		final ArrayList<Method> lowestPriority = new ArrayList<>();
		for (Method method : methods.keySet()) {
			for (Class<?> type : method.getParameterTypes()) {
				if (type == event.getClass()) {
					switch (method.getAnnotation(EventListener.class).priority()) {
						case HIGHEST: highestPriority.add(method); break;
						case HIGHT: hightPriority.add(method); break;
						case NORMAL: normalPriority.add(method); break;
						case LOW: lowPriority.add(method); break;
						case LOWEST: lowestPriority.add(method); break;
						default: break;
					}
					break;
				}
			}
		}
		highestPriority.forEach(method -> executeMethod(method, event));
		hightPriority.forEach(method -> executeMethod(method, event));
		normalPriority.forEach(method -> executeMethod(method, event));
		lowPriority.forEach(method -> executeMethod(method, event));
		lowestPriority.forEach(method -> executeMethod(method, event));
	}
	
	private static void executeMethod(Method method, Event event) {
		try {
			method.invoke(methods.get(method), event);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			exception.printStackTrace();
		}
	}

}