package eu.derzauberer.javautils.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Accessible {
	
	private Object object;
	
	public <T> Accessible(Class<T> clazz) {
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			object = constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException exception) {
			throw new IllegalArgumentException("This object does not contain a standard constructor!");
		}
	}
	
	public <T> Accessible(Class<T> clazz, Class<?> constructurTypes, Object... constructerArgs) {
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor(constructurTypes);
			constructor.setAccessible(true);
			object = constructor.newInstance(constructerArgs);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException exception) {
			throw new IllegalArgumentException("This object does not contain a constructor with these arguments!");
		}
	}
	
	public <T> Accessible(Object object) {
		this.object = object;
	}
	
	public AccessibleField getAccessibleField(String name) {
		for (AccessibleField field : getAccessibleFields()) {
			if (field.getName().equals(name)) return field; 
		}
		return null;
	}
	
	public List<String> getAccessibleFieldNames() {
		List<String> fields = new ArrayList<>();
		for (AccessibleField field : getAccessibleFields()) {
			fields.add(field.getName());
		}
		return fields;
	}
	
	public List<AccessibleField> getAccessibleFields() {
		List<AccessibleField> fields = new ArrayList<>();
		for (Field field : object.getClass().getDeclaredFields()) {
			if (getAnnotation(field) != null) {
				fields.add(new AccessibleField(object, field));
			}
		}
		Collections.sort(fields, new Comparator<AccessibleField>() {
			@Override
			public int compare(AccessibleField field1, AccessibleField field2) {
				int value1 = field1.getPosition();
				int value2 = field2.getPosition();
				if (value1 == value2) return 0;
				if (value1 < 0) return 1;
				if (value2 < 0) return -1;
				return value1 - value2;
			}
		});
		return fields;
	}
	
	public Object getObject() {
		return object;
	}
	
	private eu.derzauberer.javautils.annotations.AccessibleField getAnnotation(Field field) {
		return field.getAnnotation(eu.derzauberer.javautils.annotations.AccessibleField.class);
	}

}
