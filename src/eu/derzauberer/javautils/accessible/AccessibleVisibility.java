package eu.derzauberer.javautils.accessible;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AccessibleVisibility {
	
	Visibility fields() default Visibility.PUBLIC;
	Visibility methods() default Visibility.PUBLIC;

}
