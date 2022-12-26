package eu.derzauberer.javautils.accessible;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The class wraps the {@link Method} and make its usage easier to use.
 * 
 * @param <A> the type of the parent accessor
 */
public class MethodAccessor<A> {

	private final Method method;
	private final Accessor<A> parent;
	private final int index;
	
	/**
	 * Creates a new {@link Accessor} object with its parent and the corresponding {@link Method}.
	 * 
	 * @param method the corresponding {@link Method}
	 * @param parent the accessor of the object, which the method is part of
	 * @param index the position of this method in the class
	 */
	public MethodAccessor(Method method, Accessor<A> parent, int index) {
		this.parent = parent;
		this.method = method;
		this.index = index;
		method.setAccessible(true);
	}
	
	/**
	 * Invokes the method with the given parameters
	 * 
	 * @param args the parameters of the method
	 * @return an object, that the underlying method does return
	 * @throws IllegalArgumentException if the method couldn't be invoked due to wrong parameters
	 */
	public Object invoke(Object... args) throws IllegalArgumentException {
		try {
			return method.invoke(parent.getObject(), args);
		} catch (IllegalAccessException | InvocationTargetException exception) {
			throw new AccessorException(exception.getMessage());
		}
	}
	
	/**
	 * Returns the accessor of the object, which the method is part of.
	 * 
	 * @return the accessor of the object, which the method is part of
	 */
	public Accessor<A> getParent() {
		return parent;
	}
	
	/**
	 * Returns the the position of this method in the class.
	 * 
	 * @return the position of this method in the class
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Returns the name of the method
	 * 
	 * @return the name of the method
	 */
	public String getName() {
		return method.getName();
	}
	
	/**
	 * Returns the visibility of the method.
	 * 
	 * @return the visibility of the method
	 */
	public Visibility getVisibility() {
		return Visibility.of(method);
	}
	
	/**
	 * Returns if the method is static
	 * 
	 * @return if the method is static
	 */
	public boolean isStatic() {
		return Modifier.isStatic(method.getModifiers());
	}
	
	/**
	 * Returns the quantity of parameters for this method.
	 *
	 * @return the quantity of parameters for this method
	 */
	public int getParameterCount() {
		return method.getParameterCount();
	}

	/**
	 * Returns an array of {@link Parameter} objects that identifies the parameters
	 * of the method.
	 *
	 * @return an array of {@link Parameter} objects that identifies the parameters
	 *         of the method
	 */
	public Parameter[] getParameters() {
		return method.getParameters();
	}

	/**
	 * Returns an array of {@link Class} objects that identifies the type of the
	 * methods parameters.
	 *
	 * @return an array of {@link Class} objects that identifies the type of the
	 *         methods parameters
	 */
	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}

	/**
	 * Returns an array of {@link Type} objects that identifies the type of the
	 * methods parameters.
	 *
	 * @return an array of {@link Type} objects that identifies the type of the
	 *         methods parameters
	 */
	public Type[] getGenericParameterTypes() {
		return method.getGenericParameterTypes();
	}

	/**
	 * Returns the {@link Class} object that identifies the type of the methods
	 * return type.
	 *
	 * @return the {@link Class} object that identifies the type of the methods
	 *         return type
	 */
	public Class<?> getReturnType() {
		return method.getReturnType();
	}

	/**
	 * Returns the {@link Type} object that represents the type for the methods
	 * return type.
	 *
	 * @return the {@link Type} object that represents the type for the methods
	 *         return type
	 */
	public Type getGenericReturnType() {
		return method.getGenericReturnType();
	}
	
	/**
	 * Returns a list of all annotations that are used to describe this method.
	 * 
	 * @return a list of all annotations
	 */
	public List<Annotation> getAnnotations() {
		return Arrays.asList(method.getAnnotations());
	}
	
	/**
	 * Returns the annotation object as optional. The optional is empty if the given
	 * annotations does not exists.
	 * 
	 * @param annotationn the annotation as class
	 * @return the annotation object as optional
	 */
	public Optional<Annotation> getAnnotation(Class<Annotation> annotation) {
		return Optional.of(method.getAnnotation(annotation));
	}

}
