package eu.derzauberer.javautils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import eu.derzauberer.javautils.util.Event.EventPriority;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventListener {
	public EventPriority priority() default EventPriority.NORMAL;
}
