package eu.derzauberer.javautils.accessible;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * The class takes an object and loads it's content with reflections to make
 * fields, methods and their annotations accessible. The object class can use
 * the following annotations {@link AccessibleVisibility},
 * {@link AccessibleWhitelist} and {@link AccessibleBlacklist} to define which
 * fields and methods should be accessible.
 */
public class Accessor {

	private final Object object;
	private final List<Class<?>> classes;
	private final List<FieldAccessor> fields;
	private final List<MethodAccessor> methods;
	private final List<String> whitelist;
	private final List<String> blacklist;
	private Visibility fieldVisibility;
	private Visibility methodVisibility;

	/**
	 * Wraps the object in the {@link Accessor} to gain access to fields and
	 * methods.
	 * 
	 * @param object the object to wrap
	 */
	public Accessor(Object object) {
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
				fields.add(new FieldAccessor(this, field));
			});
			Arrays.asList(clazz.getDeclaredMethods())
					.stream()
					.filter(methodPredicate)
					.forEach(method -> {
				method.setAccessible(true);
				methods.add(new MethodAccessor(this, method));
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
	public Object getObject() {
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
	public List<FieldAccessor> getFields() {
		return new ArrayList<>(fields);
	}

	/**
	 * Returns a {@link List} of all {@link MethodAccessor}, which are usable for
	 * this class.
	 * 
	 * @return a {@link List} of all {@link MethodAccessor}, which are usable for
	 *         this class
	 */
	public List<MethodAccessor> getMethods() {
		return new ArrayList<>(methods);
	}

}
