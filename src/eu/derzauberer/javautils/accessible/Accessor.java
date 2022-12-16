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
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
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
	private final List<FieldAccessor<?, ?>> fields;
	private final List<MethodAccessor<?>> methods;
	private final List<String> whitelist;
	private final List<String> blacklist;
	private final List<Annotation> annotations;
	private final Visibility fieldVisibility;
	private final Visibility methodVisibility;

	/**
	 * Creates a new Accessor to gain access to fields and methods. This
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
	 * Creates a new Accessor to gain access to fields and methods. This
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
	 * Creates a new Accessor to gain access to fields and methods. It will uses the
	 * visibility setting of the object, which is set via annotations. If there are
	 * no annotations it will use an empty whitelist, an empty blacklist an
	 * {@link Visibility#NONE} as default parameter for fields and methods.
	 * 
	 * @param object the object to wrap
	 */
	public Accessor(T object) {
		this(object, null, null, null, null);
		loadContent();
	}

	/**
	 * Creates a new Accessor to gain access to fields and methods. All parameters
	 * except for object are nullable. If a value is null it will uses the setting
	 * of the object, which is set via annotations. If there are no annotations it
	 * will use an empty whitelist, an empty blacklist an {@link Visibility#PUBLIC} as
	 * default parameter for fields and methods.
	 * 
	 * @param type             the class to instanciate
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
	public Accessor(Class<T> type, List<String> whitelist, List<String> blacklist, Visibility fieldVisibility, Visibility methodVisibility) {
		this(instantiate(type), whitelist, blacklist, fieldVisibility, methodVisibility);
	}
	
	/**
	 * Creates a new Accessor to gain access to fields and methods. This
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
	public Accessor(Class<T> type, Class<?>[] constructorTypes, Object[] constructorArgs, List<String> whitelist, List<String> blacklist, Visibility fieldVisibility, Visibility methodVisibility) {
		this(instantiate(type, constructorTypes, constructorArgs), whitelist, blacklist, fieldVisibility, methodVisibility);
	}

	/**
	 * Creates a new Accessor to gain access to fields and methods. All parameters
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
	public Accessor(T object, List<String> whitelist, List<String> blacklist, Visibility fieldVisibility, Visibility methodVisibility) {
		this.object = object;
		this.fields = new ArrayList<>();
		this.methods = new ArrayList<>();
		this.classes = new ArrayList<>();
		this.classes.add(object.getClass());
		this.annotations = new ArrayList<>();
		final AccessibleVisibility visibilityAnnotation = object.getClass().getAnnotation(AccessibleVisibility.class);
		final AccessibleWhitelist whitelistAnnotation = object.getClass().getAnnotation(AccessibleWhitelist.class);
		final AccessibleBlacklist blacklistAnnotation = object.getClass().getAnnotation(AccessibleBlacklist.class);
		
		if (whitelist != null) this.whitelist = whitelist;
		else if (whitelistAnnotation != null) this.whitelist = Arrays.asList(whitelistAnnotation.value());
		else this.whitelist = new ArrayList<>();
		
		if (blacklist != null) this.blacklist = blacklist;
		else if (blacklistAnnotation != null) this.blacklist = Arrays.asList(blacklistAnnotation.value());
		else this.blacklist = new ArrayList<>();
		
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
	}

	/**
	 * Loads all fields and methods, which are accessible in this class.
	 */
	private void loadContent() {
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
			annotations.addAll(Arrays.asList(clazz.getAnnotations()));
			if (clazz.getAnnotation(AccessibleWhitelist.class) != null) {
				Arrays.stream(clazz.getAnnotation(AccessibleWhitelist.class).value())
					.filter(name -> !whitelist.contains(name))
					.forEach(whitelist::add);
			}
			if (clazz.getAnnotation(AccessibleBlacklist.class) != null) {
				Arrays.stream(clazz.getAnnotation(AccessibleBlacklist.class).value())
					.filter(name -> !blacklist.contains(name))
					.forEach(blacklist::add);
			}
			Arrays.stream(clazz.getDeclaredFields())
					.filter(fieldPredicate)
					.forEach(field -> {
				fields.add(new FieldAccessor<>(this, field));
			});
			Arrays.stream(clazz.getDeclaredMethods())
					.filter(methodPredicate)
					.forEach(method -> {
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
	 * Returns a {@link List} of all public {@link FieldAccessor}, which are usable for
	 * this class.
	 * 
	 * @return a {@link List} of all public{@link FieldAccessor}, which are usable for
	 *         this class
	 */
	public List<FieldAccessor<?, ?>> getPublicFields() {
		return fields.stream().filter(field -> field.getVisibility() == Visibility.PUBLIC).collect(Collectors.toList());
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
	 * Returns a {@link List} of all public {@link MethodAccessor}, which are usable for
	 * this class.
	 * 
	 * @return a {@link List} of all public {@link MethodAccessor}, which are usable for
	 *         this class
	 */
	public List<MethodAccessor<?>> getPublicMethods() {
		return methods.stream().filter(method -> method.getVisibility() == Visibility.PUBLIC).collect(Collectors.toList());
	}
	
	/**
	 * Returns if the class is an interface and can not be instantiated.
	 * 
	 * @return if the class is an interface
	 */
	public boolean isInterface() {
		return object.getClass().isInterface();
	}

	/**
	 * Returns if the class is abstract and can not be instantiated.
	 * 
	 * @return if the class is abstract
	 */
	public boolean isAbstract() {
		return Modifier.isAbstract(object.getClass().getModifiers());
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

