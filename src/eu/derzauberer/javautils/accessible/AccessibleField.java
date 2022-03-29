package eu.derzauberer.javautils.accessible;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class AccessibleField {
	
	private Object object;
	private Field field;
	
	public AccessibleField(Object object, Field field) {
		this.field = field;
		this.object = object;
		if (getAnnotation() != null) {
			field.setAccessible(true);
		} else {
			throw new IllegalArgumentException("Field does not have the @AccessableField annotation!");
		}
	}
	
	public int getPosition() {
		return getAnnotation().position();
	}
	
	public String getName() {
		return field.getName();
	}
	
	public void setValue(Object object) {
		try {
			field.set(this.object, object);
		} catch (IllegalArgumentException | IllegalAccessException exception) {
			exception.printStackTrace();
		}
	}
	
	public Object getValue() {
		try {
			return field.get(this.object);
		} catch (IllegalArgumentException | IllegalAccessException exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	public Class<?> getClassType() {
		return field.getType();
	}
	
	public Type getGenericType() {
		return field.getGenericType();
	}
	
	private eu.derzauberer.javautils.annotations.AccessibleField getAnnotation() {
		return field.getAnnotation(eu.derzauberer.javautils.annotations.AccessibleField.class);
	}

}
