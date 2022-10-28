package eu.derzauberer.javautils.accessible;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * The class wraps the {@link Field} and make its usage easer to use.
 */
public class FieldAccessor {
	
	private final Accessor parent;
	private final Field field;
	
	/**
	 * Creates a new {@link Accessor} object with it's parent and the corresponding {@link Field}.
	 * 
	 * @param parent the {@link Accessor} of the object, which the field is part of
	 * @param field the corresponding {@link Field}
	 */
	public FieldAccessor(final Accessor parent, final Field field) {
		this.parent = parent;
		this.field = field;
		
	}
	
	/**
	 * Sets the value of the {@link FieldAccessor}
	 * 
	 * @param value the new value of the field
	 * @throws IllegalArgumentException if the object is not assignable to the field
	 *                                  due to a wrong type
	 */
	public void setValue(Object value) throws IllegalArgumentException {
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
	public Object getValue() {
		try {
			return field.get(parent.getObject());
		} catch (IllegalAccessException | IllegalArgumentException exception) {
			throw new AccessorException(exception.getMessage());
		}
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
	 * Returns the {@link Accessor} parent of this {@link FieldAccessor}.
	 * 
	 * @return the {@link Accessor} parent of this {@link FieldAccessor}
	 */
	public Accessor getParent() {
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

}
