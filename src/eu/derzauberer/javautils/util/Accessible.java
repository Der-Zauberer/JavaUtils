package eu.derzauberer.javautils.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import eu.derzauberer.javautils.annotations.AccessibleField;

public interface Accessible {
	
	public static <T> T instanciate(Class<T> clazz) {
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object instance = constructor.newInstance();
			return clazz.cast(instance);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException exception) {
			return null;
		}
	}
	
	public static <T> T instanciate(Class<T> clazz, Class<?> constructurTypes, Object... constructerArgs) {
		try {
			Constructor<?> constructor = clazz.getConstructor(constructurTypes);
			constructor.setAccessible(true);
			Object instance = constructor.newInstance(constructerArgs);
			return clazz.cast(instance);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException exception) {
			return null;
		}
	}
	
	public default Field getField(String name) {
		for (Field field : getAccessibleFields()) {
			if (field.getName().equals(name)) {
				return field;
			}
		}
		return null;
	}
	
	public default int getFieldPosition(String name) {
		return getFieldPosition(getField(name));
	}
	
	public default int getFieldPosition(Field field) {
		return field.getAnnotation(AccessibleField.class).position();
	}
	
	public default void setFieldValue(String name, Object object) {
		setFieldValue(getField(name), object);
	}
	
	public default void setFieldValue(Field field, Object object) {
		try {
			field.set(this, object);
		} catch (IllegalArgumentException | IllegalAccessException exception) {
			exception.printStackTrace();
		}
	}
	
	public default Object getFieldValue(String name) {
		return getFieldValue(getField(name));
	}
	
	public default Object getFieldValue(Field field) {
		try {
			return field.get(this);
		} catch (IllegalArgumentException | IllegalAccessException exception) {
			return null;
		}
	}
	
	public default List<Field> getAccessibleFields() {
		List<Field> fields = new ArrayList<>();
		for (Field field : this.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(AccessibleField.class)) {
				field.setAccessible(true);
				fields.add(field);
			}
		}
		Collections.sort(fields, new Comparator<Field>() {
			@Override
			public int compare(Field field1, Field field2) {
				int value1 = field1.getAnnotation(AccessibleField.class).position();
				int value2 = field2.getAnnotation(AccessibleField.class).position();
				if (value1 == value2) return 0;
				if (value1 < 0) return 1;
				if (value2 < 0) return -1;
				return value1 - value2;
			}
		});
		return fields;
	}

}
