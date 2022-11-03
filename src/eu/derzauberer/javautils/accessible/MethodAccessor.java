package eu.derzauberer.javautils.accessible;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * The class wraps the {@link Method} and make its usage easer to use.
 */
public class MethodAccessor {
	
	private final Accessor parent;
	private final Method method;
	
	/**
	 * Creates a new {@link Accessor} object with it's parent and the corresponding {@link Method}.
	 * 
	 * @param parent the {@link Accessor} of the object, which the field is part of
	 * @param method the corresponding {@link Method}
	 */
	public MethodAccessor(Accessor parent, Method method) {
		this.parent = parent;
		this.method = method;
	}
	
	/**
	 * Invokes the method with the given parameters
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
	
	/**.
	 * Returns the name of the field
	 * 
	 * @return the name of the field
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

}
