package eu.derzauberer.javautils.accessible;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 The class takes an object and loads its content with reflections to make
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
	private final List<FieldAccessor<T, ?>> fieldList;
	private final Map<String, FieldAccessor<T, ?>> fieldMap;
	private final List<MethodAccessor<T>> methodList;
	private final Map<String, MethodAccessor<T>> methodMap;
	private final Set<String> whitelist;
	private final Set<String> blacklist;
	private final List<String> order;
	private final List<Annotation> annotations;
	private final Visibility fieldVisibility;
	private final Visibility methodVisibility;

	/**
	 * Creates a new accessor to gain access to fields and methods. This
	 * instantiation does only work if the class contains a default private or
	 * public constructor. It will uses the visibility setting of the object, which
	 * is set via annotations. If there are no annotations it will use an empty
	 * whitelist, an empty blacklist an {@link Visibility#PUBLIC} as default parameter
	 * for fields and methods.
	 * 
	 * @param type the type of the class to in instantiate
	 * @throws IllegalArgumentException if the wrong arguments where given to the
	 *                                  constructor
	 * @throws AccessorException        if the constructor doesn't exist or object
	 *                                  is an interface or abstract object or an
	 *                                  exception is thrown
	 */
	public Accessor(Class<T> type) {
		this(instantiate(type));
	}

	/**
	 * Creates a new accessor to gain access to fields and methods. This
	 * instantiation does only work if the class constructor with the exact same
	 * parameters. It will uses the visibility setting of the object, which is set
	 * via annotations. If there are no annotations it will use an empty whitelist,
	 * an empty blacklist an {@link Visibility#PUBLIC} as default parameter for fields
	 * and methods.
	 * 
	 * @param type             type the type of the class to in instantiate
	 * @param constructorTypes the types of the constructor arguments to identify
	 *                         the constructor
	 * @param constructorArgs  the constructor arguments
	 * @throws IllegalArgumentException if the wrong arguments where given to the
	 *                                  constructor
	 * @throws AccessorException        if the constructor doesn't exist or object
	 *                                  is an interface or abstract object or an
	 *                                  exception is thrown
	 */
	public Accessor(Class<T> type, Class<?>[] constructorTypes, Object... constructorArgs) {
		this(instantiate(type, constructorTypes, constructorArgs));
	}

	/**
	 * Creates a new accessor to gain access to fields and methods. It will uses the
	 * visibility setting of the object, which is set via annotations. If there are
	 * no annotations it will use an empty whitelist, an empty blacklist an
	 * {@link Visibility#NONE} as default parameter for fields and methods.
	 * 
	 * @param object the object to wrap
	 */
	public Accessor(T object) {
		this(object, null, null, null, null, null);
	}

	/**
	 * Creates a new accessor to gain access to fields and methods. All parameters
	 * except for object are nullable. If a value is null it will uses the setting
	 * of the object, which is set via annotations. If there are no annotations it
	 * will use an empty whitelist, an empty blacklist an {@link Visibility#PUBLIC} as
	 * default parameter for fields and methods.
	 * 
	 * @param type             the class to instantiate
	 * @param whitelist        the names of the fields and methods to only use these
	 * @param blacklist        the names of the fields and methods to ignore
	 * @param fieldVisibility  which visibility of the fields should be used
	 * @param methodVisibility which visibility of the methods should be used
	 * @throws IllegalArgumentException if the wrong arguments where given to the
	 *                                  constructor
	 * @throws AccessorException        if the constructor doesn't exist or object
	 *                                  is an interface or abstract object or an
	 *                                  exception is thrown
	 */
	public Accessor(Class<T> type, Set<String> whitelist, Set<String> blacklist, List<String> order, Visibility fieldVisibility, Visibility methodVisibility) {
		this(instantiate(type), whitelist, blacklist, order, fieldVisibility, methodVisibility);
	}
	
	/**
	 * Creates a new accessor to gain access to fields and methods. This
	 * instantiation does only work if the class constructor with the exact same
	 * parameters. All parameters except for object are nullable. If a value is null
	 * it will uses the setting of the object, which is set via annotations. If
	 * there are no annotations it will use an empty whitelist, an empty blacklist
	 * an {@link Visibility#PUBLIC} as default parameter for fields and methods.
	 * 
	 * @param type             type the type of the class to in instantiate
	 * @param constructorTypes the types of the constructor arguments to identify
	 *                         the constructor
	 * @param constructorArgs  the constructor arguments
	 * @throws IllegalArgumentException if the wrong arguments where given to the
	 *                                  constructor
	 * @throws AccessorException        if the constructor doesn't exist or object
	 *                                  is an interface or abstract object or an
	 *                                  exception is thrown
	 */
	public Accessor(Class<T> type, Class<?>[] constructorTypes, Object[] constructorArgs, Set<String> whitelist, Set<String> blacklist, List<String> order, Visibility fieldVisibility, Visibility methodVisibility) {
		this(instantiate(type, constructorTypes, constructorArgs), whitelist, blacklist, order, fieldVisibility, methodVisibility);
	}

	/**
	 * Creates a new accessor to gain access to fields and methods. All parameters
	 * except for object are nullable. If a value is null it will uses the setting
	 * of the object, which is set via annotations. If there are no annotations it
	 * will use an empty whitelist, an empty blacklist an {@link Visibility#PUBLIC} as
	 * default parameter for fields and methods.
	 * 
	 * @param object           the object to wrap
	 * @param whitelist        the names of the fields and methods to only use these
	 * @param blacklist        the names of the fields and methods to ignore
	 * @param fieldVisibility  which visibility of the fields should be used
	 * @param methodVisibility which visibility of the methods should be used
	 */
	public Accessor(T object, Set<String> whitelist, Set<String> blacklist, List<String> order, Visibility fieldVisibility, Visibility methodVisibility) {
		this.object = object;
		this.fieldList = new ArrayList<>();
		this.fieldMap = new HashMap<>();
		this.methodList = new ArrayList<>();
		this.methodMap = new HashMap<>();
		this.classes = new ArrayList<>();
		this.classes.add(object.getClass());
		this.whitelist = new HashSet<>();
		this.blacklist = new HashSet<>();
		this.order = new ArrayList<>();
		this.annotations = new ArrayList<>();
		final AccessibleVisibility visibilityAnnotation = object.getClass().getAnnotation(AccessibleVisibility.class);
		final AccessibleWhitelist whitelistAnnotation = object.getClass().getAnnotation(AccessibleWhitelist.class);
		final AccessibleBlacklist blacklistAnnotation = object.getClass().getAnnotation(AccessibleBlacklist.class);
		final AccessibleOrder orderAnnotation = object.getClass().getAnnotation(AccessibleOrder.class);
		
		if (whitelist != null) this.whitelist.addAll(whitelist);
		if (whitelistAnnotation != null) this.whitelist.addAll(Arrays.asList(whitelistAnnotation.value()));
		
		if (blacklist != null) this.blacklist.addAll(blacklist);
		if (blacklistAnnotation != null) this.blacklist.addAll(Arrays.asList(blacklistAnnotation.value()));
		
		if (order != null) this.order.addAll(order);
		if (orderAnnotation != null) this.order.addAll(Arrays.asList(orderAnnotation.value()));
		
		if (fieldVisibility != null) this.fieldVisibility = fieldVisibility;
		else if (visibilityAnnotation != null) this.fieldVisibility = visibilityAnnotation.fields();
		else this.fieldVisibility = Visibility.PUBLIC;
		
		if (methodVisibility != null) this.methodVisibility = methodVisibility;
		else if (visibilityAnnotation != null) this.methodVisibility = visibilityAnnotation.methods();
		else this.methodVisibility = Visibility.PUBLIC;
		
		Class<?> superclass;
		while ((superclass = classes.get(classes.size() - 1).getSuperclass()) != null
				&& !superclass.equals(Object.class)) {
			classes.addAll(Arrays.asList(superclass.getInterfaces()));
			classes.add(superclass);
		}
		Collections.reverse(classes);
		loadContent();
	}

	/**
	 * Loads all fields and methods, which are accessible in this class.
	 */
	private void loadContent() {
		for (Class<?> clazz : classes) {
			annotations.addAll(Arrays.asList(clazz.getAnnotations()));
			final Field[] fieldArray = sortFields(clazz.getDeclaredFields());
			for (int i = 0; i < fieldArray.length; i++) {
				if (fieldVisibility != Visibility.ANY && fieldVisibility != Visibility.of(fieldArray[i]) && !blacklist.contains(fieldArray[i].getName())) continue;
				if (blacklist.contains(fieldArray[i].getName())) continue;
				final FieldAccessor<T, ?> field = new FieldAccessor<>(fieldArray[i], this, i);
				fieldList.add(field);
				fieldMap.put(fieldArray[i].getName(), field);
			}
			final Method[] methodArray = sortMethods(clazz.getDeclaredMethods());
			for (int i = 0; i < methodArray.length; i++) {
				if (methodVisibility != Visibility.ANY && methodVisibility != Visibility.of(methodArray[i]) && !blacklist.contains(methodArray[i].getName())) continue;
				if (blacklist.contains(methodArray[i].getName())) continue;
				final MethodAccessor<T> method = new MethodAccessor<>(methodArray[i], this, i);
				methodList.add(method);
				methodMap.put(methodArray[i].getName(), method);
			}
		}
	}
	
	/**
	 * Sorts all fields in the order of the {@link #order} list.
	 * 
	 * @param fieldArray the array to sort
	 * @return the sorted array
	 */
	private Field[] sortFields(Field[] fieldArray) {
		if (order.isEmpty()) return fieldArray;
		final Field[] fields = fieldArray.clone();
		final HashMap<String, Integer> fieldMap = new HashMap<>(fields.length);
		for (int i = 0; i < fields.length; i++) {
			fieldMap.put(fields[i].getName(), i);
		}
		final Field[] resultFields = new Field[fields.length];
		int index = 0;
		for (String name : order) {
			final Integer fieldIndex = fieldMap.get(name);
			if (fieldIndex == null) continue;
			final Field field = fields[fieldIndex];
			resultFields[index++] = field;
			fields[fieldIndex] = null;
			fieldMap.remove(name);
		}
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] == null) continue;
			resultFields[index++] = fields[i];
		}
		return resultFields;
	}
	
	/**
	 * Sorts all methods in the order of the {@link #order} list.
	 * 
	 * @param methodArray the array to sort
	 * @return the sorted array
	 */
	private Method[] sortMethods(Method[] methodArray) {
		if (order.isEmpty()) return methodArray;
		final Method[] methods = methodArray.clone();
		final HashMap<String, Integer> methodMap = new HashMap<>(methods.length);
		for (int i = 0; i < methods.length; i++) {
			methodMap.put(methods[i].getName(), i);
		}
		final Method[] resultMethods = new Method[methods.length];
		int index = 0;
		for (String name : order) {
			final Integer fieldIndex = methodMap.get(name);
			if (fieldIndex == null) continue;
			final Method field = methods[fieldIndex];
			resultMethods[index++] = field;
			methods[fieldIndex] = null;
			methodMap.remove(name);
		}
		for (int i = 0; i < methods.length; i++) {
			if (methods[i] == null) continue;
			resultMethods[index++] = methods[i];
		}
		return resultMethods;
	}

	/**
	 * Returns the name of the object, that is wrapped in the {@link Accessor}.
	 * 
	 * @return the name of the object, that is wrapped in the accessor
	 */
	public String getName() {
		return object.getClass().getSimpleName();
	}
	
	/**
	 * Returns the wrapped object of the {@link Accessor}.
	 * 
	 * @return the wrapped object of the accessor
	 */
	public T getObject() {
		return object;
	}

	/**
	 * Returns the {@link Class} of the object, that is wrapped in the
	 * {@link Accessor}.
	 * 
	 * @return the class of the object, that is wrapped in the accessor
	 */
	public Class<?> getClassType() {
		return object.getClass();
	}

	/**
	 * Returns a {@link List} of classes. That contains the host class and
	 * superclasses and interfaces. The classes are sorted in the order
	 * they are loaded.
	 * 
	 * @return a list of classes such as the host class and supercass and
	 *         interfaces
	 */
	public List<Class<?>> getClasses() {
		return new ArrayList<>(classes);
	}

	/**
	 * Returns a {@link List} of all {@link FieldAccessor}, which are usable for
	 * this class.
	 * 
	 * @return a list of all fields, which are usable for
	 *         this class
	 */
	public List<FieldAccessor<T, ?>> getFields() {
		return new ArrayList<>(fieldList);
	}
	
	/**
	 * Returns a {@link List} of all public {@link FieldAccessor}, which are usable for
	 * this class.
	 * 
	 * @return a list of all public fields, which are usable for
	 *         this class
	 */
	public List<FieldAccessor<T, ?>> getPublicFields() {
		return fieldList.stream().filter(field -> field.getVisibility() == Visibility.PUBLIC).collect(Collectors.toList());
	}
	
	/**
	 * Returns an {@link Optional} of the field by name. The time is
	 * constant due to a {@link HashMap}.
	 * 
	 * @param name the name of the field
	 * @return an optional of the field
	 */
	public Optional<FieldAccessor<T, ?>> getField(String name) {
		return Optional.of(fieldMap.get(name));
	}
	
	/**
	 * Returns an {@link Optional} of the public field by name. The time is
	 * constant due to a {@link HashMap}.
	 * 
	 * @param name the name of the field
	 * @return an optional of the field
	 */
	public Optional<FieldAccessor<T, ?>> getPublicField(String name) {
		final FieldAccessor<T, ?> field = fieldMap.get(name);
		return field != null && field.getVisibility() == Visibility.PUBLIC ? Optional.of(field) : Optional.empty();
	}
	
	/**
	 * Returns a {@link List} of all {@link MethodAccessor}, which are usable for
	 * this class.
	 * 
	 * @return a list of all methods, which are usable for
	 *         this class
	 */
	public List<MethodAccessor<T>> getMethods() {
		return new ArrayList<>(methodList);
	}

	/**
	 * Returns a {@link List} of all public {@link MethodAccessor}, which are usable for
	 * this class.
	 * 
	 * @return a list of all public method, which are usable for
	 *         this class
	 */
	public List<MethodAccessor<T>> getPublicMethods() {
		return methodList.stream().filter(method -> method.getVisibility() == Visibility.PUBLIC).collect(Collectors.toList());
	}
	
	/**
	 * Returns an {@link Optional} of the method by name. The time is
	 * constant due to a {@link HashMap}.
	 * 
	 * @param name the name of the method
	 * @return an optional of the method
	 */
	public Optional<MethodAccessor<T>> getMethod(String name) {
		return Optional.of(methodMap.get(name));
	}
	
	/**
	 * Returns an {@link Optional} of the public method by name. The time is
	 * constant due to a {@link HashMap}.
	 * 
	 * @param name the name of the method
	 * @return an optional of the method
	 */
	public Optional<MethodAccessor<T>> getMethodField(String name) {
		final MethodAccessor<T> method = methodMap.get(name);
		return method != null && method.getVisibility() == Visibility.PUBLIC ? Optional.of(method) : Optional.empty();
	}
	
	/**
	 * Returns a list of all annotations that are used to describe this field. The
	 * superclass annotation are stores before the annotations of the host object.
	 * 
	 * @return a list of all annotations
	 */
	public List<Annotation> getAnnotations() {
		return new ArrayList<>(annotations);
	}
	
	/**
	 * Returns the annotation object as optional. The optional is empty if the given
	 * annotations does not exists. If a superclass an the host defines the same
	 * annotation the one of the superclass will be used.
	 * 
	 * @param annotationn the annotation as class
	 * @return the annotation object as optional
	 */
	public Optional<Annotation> getAnnotation(Class<Annotation> annotation) {
		return annotations.stream().filter(entity -> entity.getClass().equals(annotation)).findFirst();
	}
	
	/**
	 * Tries to create an instance of the type. This instantiation does only work if
	 * the class has a standard constructor.
	 *
	 * @param <T>  the type of the object inside the accessor
	 * @param type the type of the class to in instantiate
	 * @return the instantiated object
	 * @throws AccessorException if the constructor doesn't exist or object is an
	 *                           interface or abstract object or an exception is
	 *                           thrown
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instantiate(Class<T> type) {
		Constructor<?> constructor;
		try {
			constructor = type.getDeclaredConstructor();
			constructor.setAccessible(true);
			return (T) constructor.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException exception) {
			if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
				throw new AccessorException("Can't instantiate abstract or interface object " + type.getName() + "!", exception);
			} else {
				throw new AccessorException("Can't instantiate the " + type.getName() + " object, it must contain at least a private default constructor!", exception);
			}
		} catch (InvocationTargetException exception) {
			throw new AccessorException("An exception was thrown on the instantiation of " + type.getName() + "!", exception);
		}
	}
	
	/**
	 * Tries to create an instance of the type. This instantiation does only work if
	 * the class constructor with the exact same parameters.
	 * 
	 * @param <T>              the type of the object inside the accessor
	 * @param type             type the type of the class to in instantiate
	 * @param constructorTypes the types of the constructor arguments to identify
	 *                         the constructor
	 * @param constructorArgs  the constructor arguments
	 * @return the instantiated object
	 * @throws IllegalArgumentException  if the wrong arguments where given to the
	 *                                   constructor
	 * @throws AccessorException         if the constructor doesn't exist or object
	 *                                   is an interface or abstract object or an
	 *                                   exception is thrown
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instantiate(Class<T> type, Class<?>[] constructorTypes, Object... constructorArgs) {		
		Constructor<?> constructor;
		try {
			constructor = type.getDeclaredConstructor(constructorTypes);
			constructor.setAccessible(true);
			return (T) constructor.newInstance(constructorArgs);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException exception) {
			if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
				throw new AccessorException("Can't instantiate abstract or interface object " + type.getName() + "!", exception);
			} else {
				throw new AccessorException("Can't instantiate the " + type.getName() + " object, it must contain a constructor with the given arguments!", exception);
			}
		} catch (InvocationTargetException exception) {
			throw new AccessorException("An exception was thrown on the instantiation of " + type.getName() + "!", exception);
		}
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
	public static <T> T[] instantiateArray(Class<T> type, int size) {
		return (T[]) Array.newInstance(type, size);
	}

}
