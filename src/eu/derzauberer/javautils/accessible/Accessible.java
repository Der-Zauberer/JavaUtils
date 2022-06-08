package eu.derzauberer.javautils.accessible;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import eu.derzauberer.javautils.annotations.AccessibleGetter;
import eu.derzauberer.javautils.annotations.AccessibleSetter;

public class Accessible {
	
	private Object object;
	private ArrayList<AccessibleField> fields;
	private ArrayList<AccessibleMethod> methods;
	
	public <T> Accessible(Class<T> clazz) {
		try {
			final Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			object = constructor.newInstance();
			parseContent();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException exception) {
			throw new AccessibleException("This object does not contain a standard constructor!");
		}
	}
	
	public <T> Accessible(Class<T> clazz, Class<?> constructurTypes, Object... constructerArgs) {
		try {
			final Constructor<?> constructor = clazz.getDeclaredConstructor(constructurTypes);
			constructor.setAccessible(true);
			object = constructor.newInstance(constructerArgs);
			parseContent();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException exception) {
			throw new AccessibleException("This object does not contain a constructor with these arguments!");
		}
	}
	
	public <T> Accessible(Object object) {
		this.object = object;
		parseContent();
	}
	
	private void parseContent() {
		fields = new ArrayList<>();
		methods = new ArrayList<>();
		final ArrayList<String> names = new ArrayList<>();
		final ArrayList<Class<?>> classes = new ArrayList<>();
		classes.add(object.getClass());
		Class<?> superclass;
		while ((superclass = classes.get(classes.size() - 1).getSuperclass()) != null) {
			for (Class<?> interfaze : superclass.getInterfaces()) classes.add(interfaze);
			classes.add(superclass);
		}
		Collections.reverse(classes);
		for (Class<?> clazz : classes) {
			for (Field field :clazz.getDeclaredFields()) {
				if (getAnnotation(field) != null) {
					AccessibleField accessibleField = new AccessibleField(object, field);
					if (names.contains(accessibleField.getName())) {
						throw new AccessibleException("The name " + accessibleField.getName() + "is used two times! Names have to be individual!");
					} else {
						fields.add(accessibleField);
						names.add(accessibleField.getName());
					}
				}
				
			}
			final HashMap<String, Method> getterAndSetters = new HashMap<>();
			for (Method method : clazz.getDeclaredMethods()) {
				String name = null;
				boolean isSetter = true;
				if (method.isAnnotationPresent(AccessibleSetter.class)) {
					name = method.getAnnotation(AccessibleSetter.class).name();
				} else if (method.isAnnotationPresent(AccessibleGetter.class)) {
					name = method.getAnnotation(AccessibleGetter.class).name();
					isSetter = false;
				} else if (getAnnotation(method) != null) {
					final AccessibleMethod accessibleMethod = new AccessibleMethod(object, method);
					if (names.contains(accessibleMethod.getName())) {
						throw new AccessibleException("The name " + accessibleMethod.getName() + "is used two times! Names have to be individual!");
					} else {
						methods.add(accessibleMethod);
						names.add(accessibleMethod.getName());
					}
				}
				if (name != null) {
					if (name.isEmpty()) throw new AccessibleException(((isSetter) ? "Setter " : "Getter ") + method.getName() + " does not have a name!");
					if (names.contains(name)) {
						if (getterAndSetters.get(name) != null) {
							AccessibleField field = null;
							if (isSetter) {
								field = new AccessibleField(object, method, getterAndSetters.get(name));
							} else {
								field = new AccessibleField(object, getterAndSetters.get(name), method);
							}
							fields.add(field);
							getterAndSetters.remove(name);
						} else {
							throw new AccessibleException("A field with the name " + name + " does already exits!");
						}
					} else {
						names.add(name);
						getterAndSetters.put(name, method);
					}
				}
			}
		}
		fields.sort((accessibleField1, accessibleField2) -> accessibleField1.getPosition() - accessibleField2.getPosition());
		methods.sort((accessibleMethod1, accessibleMethod2) -> accessibleMethod1.getName().compareTo(accessibleMethod2.getName()));
	}

	public AccessibleField getAccessibleField(String name) {
		for (AccessibleField field : fields) {
			if (field.getName().equals(name)) return field; 
		}
		return null;
	}
	
	public List<AccessibleField> getAccessibleFields() {
		return fields;
	}
	
	public List<String> getAccessibleFieldNames() {
		final List<String> names = new ArrayList<>();
		for (AccessibleField field : fields) {
			names.add(field.getName());
		}
		return names;
	}
	
	public AccessibleMethod getAccessibleMethod(String name) {
		for (AccessibleMethod method : methods) {
			if (method.getName().equals(name)) return method; 
		}
		return null;
	}
	
	public List<AccessibleMethod> getAccessibleMethods() {
		return methods;
	}
	
	public List<String> getAccessibleMethodNames() {
		final List<String> names = new ArrayList<>();
		for (AccessibleMethod method : methods) {
			names.add(method.getName());
		}
		return names;
	}
	
	public Object getObject() {
		return object;
	}
	
	private eu.derzauberer.javautils.annotations.AccessibleField getAnnotation(Field field) {
		return field.getAnnotation(eu.derzauberer.javautils.annotations.AccessibleField.class);
	}
	
	private eu.derzauberer.javautils.annotations.AccessibleMethod getAnnotation(Method method) {
		return method.getAnnotation(eu.derzauberer.javautils.annotations.AccessibleMethod.class);
	}

}
