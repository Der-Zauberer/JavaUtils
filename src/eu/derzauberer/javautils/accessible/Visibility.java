package eu.derzauberer.javautils.accessible;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public enum Visibility {
	
	NONE,
	PRIVATE,
	PROTECTED,
	PUBLIC,
	ANY;
	
	public static Visibility of(Field field) {
		if (Modifier.isPrivate(field.getModifiers())) return PRIVATE;
		if (Modifier.isProtected(field.getModifiers())) return PROTECTED;
		if (Modifier.isPublic(field.getModifiers())) return PUBLIC;
		return ANY;
	}
	
	public static Visibility of(Method method) {
		if (Modifier.isPrivate(method.getModifiers())) return PRIVATE;
		if (Modifier.isProtected(method.getModifiers())) return PROTECTED;
		if (Modifier.isPublic(method.getModifiers())) return PUBLIC;
		return ANY;
	}
	
}
