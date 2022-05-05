package eu.derzauberer.javautils.accessible;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import eu.derzauberer.javautils.annotations.AccessibleGetter;
import eu.derzauberer.javautils.annotations.AccessibleSetter;

public class AccessibleField {
	
	private String name;
	private Object parent;
	private Field field;
	private Method setter;
	private Method getter;
	
	public AccessibleField(Object parent, Field field) {
		this.parent = parent;
		this.field = field;
		if (getAnnotation() != null) {
			field.setAccessible(true);
			if (!getAnnotation().name().isEmpty()) {
				name = getAnnotation().name();
			} else {
				name = field.getName();
			}
		} else {
			throw new AccessibleException("The field " + field.getName() + " does not have an @AccessableField annotation!");
		}		
	}
	
	public AccessibleField(Object parent, Method setter, Method getter) {
		this.parent = parent;
		this.setter = setter;
		this.getter = getter;
		AccessibleSetter setterAnnotation = setter.getAnnotation(AccessibleSetter.class);
		AccessibleGetter getterAnnotation = getter.getAnnotation(AccessibleGetter.class);
		if (setterAnnotation == null) throw new AccessibleException("Setter does not have an @AccessableSetter annotation!");
		else if (getterAnnotation == null) throw new AccessibleException("Getter does not have an @AccessableSetter annotation!");
		else if (setterAnnotation.name().isEmpty()) throw new AccessibleException("The name of the setter in the annotation schould not be empty!");
		else if (getterAnnotation.name().isEmpty()) throw new AccessibleException("The name of the getter in the annotation schould not be empty!");
		else if (!setterAnnotation.name().equals(getterAnnotation.name())) throw new AccessibleException("The name of the getter and setter in the annotations are not the same!");
		else if (!(setter.getParameterCount() == 1 && setter.getParameterTypes()[0] == getter.getReturnType())) throw new AccessibleException("The return type of the getter and the setter argument are not the same!");
		setter.setAccessible(true);
		getter.setAccessible(true);
		name = setterAnnotation.name();
	}
	
	public int getPosition() {
		if (field != null) {
			return getAnnotation().position();
		} else {
			return getter.getAnnotation(AccessibleGetter.class).position();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setValue(Object value) {
		try {
			if (field != null) {
				field.set(this.parent, value);
			} else {
				setter.invoke(parent, value);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new AccessibleException(exception.getMessage());
		}
	}
	
	public Object getValue() {
		try {
			if (field != null) {
				return field.get(this.parent);
			} else {
				return getter.invoke(parent);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new AccessibleException(exception.getMessage());
		}
	}
	
	public Class<?> getClassType() {
		if (field != null) {
			return field.getType();
		} else {
			return getter.getReturnType();
		}
		
	}
	
	public Type getGenericType() {
		if (field != null) {
			return field.getGenericType();
		} else {
			return getter.getGenericReturnType();
		}
	}
	
	private eu.derzauberer.javautils.annotations.AccessibleField getAnnotation() {
		return field.getAnnotation(eu.derzauberer.javautils.annotations.AccessibleField.class);
	}

}
