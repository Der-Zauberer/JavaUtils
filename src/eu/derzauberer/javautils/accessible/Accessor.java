package eu.derzauberer.javautils.accessible;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 The class takes an object and loads it's content with reflections to make
 * fields, methods and their annotations accessible. The object class can use
 * the following annotations {@link AccessibleVisibility},
 * {@link AccessibleWhitelist} and {@link AccessibleBlacklist} to define which
 * fields and methods should be accessible.
 *
 * @param <T> the type of the object inside the accessor
 */
public class Accessor<T> {

	private final T object;
	private final List<Class<?>> classes;
	private final List<FieldAccessor<?, ?>> fields;
	private final List<MethodAccessor<?>> methods;
	private final List<String> whitelist;
	private final List<String> blacklist;
	private Visibility fieldVisibility;
	private Visibility methodVisibility;

	/**
	 * Tries to create an instance of the type. This instantiation does only work if
	 * the class has a standard constructor.
	 * 
	 * @param type the type of the class to in instantiate
	 * @return the instantiated object
	 * @throws NoSuchMethodException     if there is no standard constructor in the
	 *                                   class
	 * @throws InstantiationException    if the class is an interface or abstract
	 *                                   class
	 * @throws IllegalAccessException    if this C constructor object is enforcing
	 *                                   Java language access control and the
	 *                                   underlying constructor is inaccessible
	 * @throws InvocationTargetException if the constructor throws an exception
	 */
	public Accessor(Class<T> type) 
			throws NoSuchMethodException, InstantiationException, 
			IllegalAccessException, InvocationTargetException {
		this(instantiate(type));
	}

	/**
	 * Tries to create an instance of the type. This instantiation does only work if
	 * the class constructor with the exact same parameters.
	 * 
	 * @param type             type the type of the class to in instantiate
	 * @param constructurTypes the types of the constructor arguments to identify
	 *                         the constructor
	 * @param constructerArgs  the constructor arguments
	 * @return the instantiated object
	 * @throws NoSuchMethodException     if there is no constructor with the given
	 *                                   argument types in the class
	 * @throws InstantiationException    if the class is an interface or abstract
	 *                                   class
	 * @throws IllegalAccessException    if this C constructor object is enforcing
	 *                                   Java language access control and the
	 *                                   underlying constructor is inaccessible
	 * @throws IllegalArgumentException  if the wrong arguments where given to the
	 *                                   constructor
	 * @throws InvocationTargetException if the constructor throws an exception
	 */
	public Accessor(Class<T> type, Class<?> constructurTypes, Object... constructerArgs) 
			throws NoSuchMethodException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this(instantiate(type, constructurTypes, constructerArgs));
	}
	
	/**
	 * Wraps the object in the {@link Accessor} to gain access to fields and
	 * methods.
	 * 
	 * @param object the object to wrap
	 */
	public Accessor(T object) {
		this.object = object;
		fields = new ArrayList<>();
		methods = new ArrayList<>();
		classes = new ArrayList<>();
		classes.add(object.getClass());
		whitelist = new ArrayList<>();
		blacklist = new ArrayList<>();
		fieldVisibility = Visibility.NONE;
		methodVisibility = Visibility.NONE;
		if (object.getClass().getAnnotation(AccessibleVisibility.class) != null) {
			fieldVisibility = object.getClass().getAnnotation(AccessibleVisibility.class).fields();
			methodVisibility = object.getClass().getAnnotation(AccessibleVisibility.class).methods();
		}
		Class<?> superclass;
		while ((superclass = classes.get(classes.size() - 1).getSuperclass()) != null
				&& !superclass.equals(Object.class)) {
			for (Class<?> interfaze : superclass.getInterfaces()) {
				classes.add(interfaze);
			}
			classes.add(superclass);
		}
		Collections.reverse(classes);
		loadContents();
	}

	/**
	 * Loads all fields and methods, which are accessible in this class.
	 */
	private void loadContents() {
		final Predicate<Field> fieldPredicate = field -> 
			(fieldVisibility == Visibility.ANY || 
			fieldVisibility == Visibility.of(field) || 
			whitelist.contains(field.getName())) &&
			!blacklist.contains(field.getName());
		final Predicate<Method> methodPredicate = method -> 
			(methodVisibility == Visibility.ANY || 
			methodVisibility == Visibility.of(method) || 
			whitelist.contains(method.getName())) 
			&& !blacklist.contains(method.getName());
		for (Class<?> clazz : classes) {
			if (clazz.getAnnotation(AccessibleWhitelist.class) != null) {
				Arrays.asList(clazz.getAnnotation(AccessibleWhitelist.class).value())
					.stream()
					.filter(name -> !whitelist.contains(name))
					.forEach(whitelist::add);
			}
			if (clazz.getAnnotation(AccessibleBlacklist.class) != null) {
				Arrays.asList(clazz.getAnnotation(AccessibleBlacklist.class).value())
					.stream()
					.filter(name -> !blacklist.contains(name))
					.forEach(blacklist::add);
			}
			Arrays.asList(clazz.getDeclaredFields())
					.stream()
					.filter(fieldPredicate)
					.forEach(field -> {
				field.setAccessible(true);
				fields.add(new FieldAccessor<>(this, field));
			});
			Arrays.asList(clazz.getDeclaredMethods())
					.stream()
					.filter(methodPredicate)
					.forEach(method -> {
				method.setAccessible(true);
				methods.add(new MethodAccessor<>(this, method));
			});
		}
	}

	/**
	 * Returns the name of the object, that is wrapped in the {@link Accessor}.
	 * 
	 * @return the name of the object, that is wrapped in the {@link Accessor}
	 */
	public String getName() {
		return object.getClass().getSimpleName();
	}
	
	/**
	 * Returns the wrapped object of the {@link Accessor}.
	 * 
	 * @return the wrapped object of the {@link Accessor}
	 */
	public T getObject() {
		return object;
	}

	/**
	 * Returns the {@link Class} of the object, that is wrapped in the
	 * {@link Accessor}.
	 * 
	 * @return the {@link Class} of the object, that is wrapped in the
	 *         {@link Accessor}
	 */
	public Class<?> getClassType() {
		return object.getClass();
	}

	/**
	 * Returns a {@link List} of classes, which the object in the {@link Accessor}
	 * is extending or implementing.
	 * 
	 * @return a {@link List} of classes, which the object in the {@link Accessor}
	 *         is extending or implementing
	 */
	public List<Class<?>> getClasses() {
		return new ArrayList<>(classes);
	}

	/**
	 * Returns a {@link List} of all {@link FieldAccessor}, which are usable for
	 * this class.
	 * 
	 * @return a {@link List} of all {@link FieldAccessor}, which are usable for
	 *         this class
	 */
	public List<FieldAccessor<?, ?>> getFields() {
		return new ArrayList<>(fields);
	}

	/**
	 * Returns a {@link List} of all {@link MethodAccessor}, which are usable for
	 * this class.
	 * 
	 * @return a {@link List} of all {@link MethodAccessor}, which are usable for
	 *         this class
	 */
	public List<MethodAccessor<?>> getMethods() {
		return new ArrayList<>(methods);
	}
	
	/**
	 * Returns if the class is an interface an can not be instantiated.
	 * 
	 * @return if the class is an interface
	 */
	public boolean isInterface() {
		return object.getClass().isInterface();
	}

	/**
	 * Returns if the class is abstract an can not be instantiated.
	 * 
	 * @return if the class is abstract
	 */
	public boolean isAbstract() {
		return Modifier.isAbstract(object.getClass().getModifiers());
	}
	
	/**
	 * Tries to create an instance of the type. This instantiation does only work if
	 * the class has a standard constructor.
	 *
	 * @param <T>  the type of the object inside the accessor
	 * @param type the type of the class to in instantiate
	 * @return the instantiated object
	 * @throws NoSuchMethodException     if there is no standard constructor in the
	 *                                   class
	 * @throws SecurityException         if the accessor has no permission to access
	 *                                   the standard constructor
	 * @throws InstantiationException    if the class is an interface or abstract
	 *                                   class
	 * @throws IllegalAccessException    if this C constructor object is enforcing
	 *                                   Java language access control and the
	 *                                   underlying constructor is inaccessible
	 * @throws InvocationTargetException if the constructor throws an exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instantiate(Class<T> type) 
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		final Constructor<?> constructor = type.getDeclaredConstructor();
		constructor.setAccessible(true);
		return (T) constructor.newInstance();
	}
	
	/**
	 * Tries to create an instance of the type. This instantiation does only work if
	 * the class constructor with the exact same parameters.
	 * 
	 * @param <T>              the type of the object inside the accessor
	 * @param type             type the type of the class to in instantiate
	 * @param constructurTypes the types of the constructor arguments to identify
	 *                         the constructor
	 * @param constructerArgs  the constructor arguments
	 * @return the instantiated object
	 * @throws NoSuchMethodException     if there is no constructor with the given
	 *                                   argument types in the class
	 * @throws InstantiationException    if the class is an interface or abstract
	 *                                   class
	 * @throws IllegalAccessException    if this C constructor object is enforcing
	 *                                   Java language access control and the
	 *                                   underlying constructor is inaccessible
	 * @throws IllegalArgumentException  if the wrong arguments where given to the
	 *                                   constructor
	 * @throws InvocationTargetException if the constructor throws an exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instantiate(Class<T> type, Class<?> constructurTypes, Object... constructerArgs) 
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Constructor<?> constructor = type.getDeclaredConstructor(constructurTypes);
		constructor.setAccessible(true);
		return (T) constructor.newInstance(constructerArgs);
	}
	
	/**
	 * Creates a new array by instantiating it.
	 * 
	 * @param <T>  the type of the array
	 * @param type the type of the array
	 * @param size the size of the array
	 * @return the array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] instanciateArray(Class<T> type, int size) {
		return (T[]) Array.newInstance(type, size);
	}

}
