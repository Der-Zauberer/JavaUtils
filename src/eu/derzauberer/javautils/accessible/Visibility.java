package eu.derzauberer.javautils.accessible;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * The enum defines the visibility of fields and methods.
 */
public enum Visibility {
	
	NONE,
	PRIVATE,
	PROTECTED,
	PUBLIC,
	ANY;
	
	/**
	 * Returns the visibility of a {@link Field}.
	 * 
	 * @param field the field from which the visibility is requested
	 * @return the visibility of a {@link Field}
	 */
	public static Visibility of(Field field) {
		if (Modifier.isPrivate(field.getModifiers())) return PRIVATE;
		if (Modifier.isProtected(field.getModifiers())) return PROTECTED;
		if (Modifier.isPublic(field.getModifiers())) return PUBLIC;
		return ANY;
	}
	
	/**
	 * Returns the visibility of a {@link Method}.
	 * 
	 * @param method the method from which the visibility is requested
	 * @return the visibility of a {@link Method}
	 */
	public static Visibility of(Method method) {
		if (Modifier.isPrivate(method.getModifiers())) return PRIVATE;
		if (Modifier.isProtected(method.getModifiers())) return PROTECTED;
		if (Modifier.isPublic(method.getModifiers())) return PUBLIC;
		return ANY;
	}
	
}
