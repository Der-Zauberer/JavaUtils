package eu.derzauberer.javautils.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eu.derzauberer.javautils.events.Event.EventPriority;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventListener {
	public EventPriority priority() default EventPriority.NORMAL;
}
