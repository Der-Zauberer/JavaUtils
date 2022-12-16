package eu.derzauberer.javautils.accessible;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The class wraps the {@link Field} and make its usage easier to use.
 *
 * @param <A> the type of the parent accessor
 * @param <F> the type of the fields value
 */
public class FieldAccessor<A extends Accessor<?>, F> {
	
	private final A parent;
	private final Field field;
	
	/**
	 * Creates a new {@link Accessor} object with its parent and the corresponding {@link Field}.
	 * 
	 * @param parent the {@link Accessor} of the object, which the field is part of
	 * @param field the corresponding {@link Field}
	 */
	public FieldAccessor(A parent, Field field) {
		this.parent = parent;
		this.field = field;
		field.setAccessible(true);
	}
	
	/**
	 * Sets the value of the {@link FieldAccessor}
	 * 
	 * @param value the new value of the field
	 * @throws IllegalArgumentException if the object is not assignable to the field
	 *                                  due to a wrong type
	 */
	public void setValue(F value) throws IllegalArgumentException {
		try {
			field.set(parent.getObject(), value);
		} catch (IllegalAccessException exception) {
			throw new AccessorException(exception.getMessage());
		}
	}
	
	/**
	 * Sets the value of the {@link FieldAccessor}
	 * 
	 * @param value the new value of the field
	 * @throws IllegalArgumentException if the object is not assignable to the field
	 *                                  due to a wrong type
	 */
	public void setObjectValue(Object value) throws IllegalArgumentException {
		try {
			field.set(parent.getObject(), value);
		} catch (IllegalAccessException exception) {
			throw new AccessorException(exception.getMessage());
		}
	}
	
	/**
	 * Returns the value of the {@link FieldAccessor}.
	 * 
	 * @return the value of the {@link FieldAccessor}
	 */
	@SuppressWarnings("unchecked")
	public F getValue() {
		try {
			return (F) field.get(parent.getObject());
		} catch (IllegalAccessException | IllegalArgumentException exception) {
			throw new AccessorException(exception.getMessage());
		}
	}
	
	/**
	 * Returns if the value of the field is present and not null.
	 * 
	 * @return if the value of the field is present and not null
	 */
	public boolean isPresent() {
		return getValue() != null;
	}
	
	/**.
	 * Returns the name of the field
	 * 
	 * @return the name of the field
	 */
	public String getName() {
		return field.getName();
	}
	
	/**
	 * Returns the visibility of the field.
	 * 
	 * @return the visibility of the field
	 */
	public Visibility getVisibility() {
		return Visibility.of(field);
	}
	
	/**
	 * Returns if the field is static
	 * 
	 * @return if the field is static
	 */
	public boolean isStatic() {
		return Modifier.isStatic(field.getModifiers());
	}
	
	/**
	 * Returns if the field is final
	 * 
	 * @return if the field is final
	 */
	public boolean isFinal() {
		return Modifier.isFinal(field.getModifiers());
	}
	
	/**
	 * Returns the {@link Accessor} parent of this {@link FieldAccessor}.
	 * 
	 * @return the {@link Accessor} parent of this {@link FieldAccessor}
	 */
	public Accessor<?> getParent() {
		return parent;
	}

	/**
     * Returns the {@link Class} object that identifies the
     * declared type for the field represented by this
     * {@link Field} object.
     *
     * @return the {@link Class} object identifying the declared
     * type of the field represented by this object
     */
	public Class<?> getClassType() {
		return field.getType();
	}
	
	/**
     * Returns the {@link Type} object that represents the declared type for
     * the field represented by this {@link Field} object.
     *
     * @return the {@link Type} object that represents the declared type for
     * the field represented by this {@link Field} object
     */
	public Type getGenericType() {
		return field.getGenericType();
	}
	
	/**
	 * Returns a list of all annotations that are used to describe this field.
	 * 
	 * @return a list of all annotations
	 */
	public List<Annotation> getAnnotations() {
		return Arrays.asList(field.getAnnotations());
	}
	
	/**
	 * Returns the annotation object as optional. The optional is empty if the given
	 * annotations does not exists.
	 * 
	 * @param annotationn the annotation as class
	 * @return the annotation object as optional
	 */
	public Optional<Annotation> getAnnotation(Class<Annotation> annotation) {
		return Optional.of(field.getAnnotation(annotation));
	}

}
