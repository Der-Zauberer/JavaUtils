package eu.derzauberer.javautils.accessible;

/**
 * An exception, which is thrown, when another exception was thrown inside the
 * functions of the {@link Accessor}, {@link FieldAccessor} or
 * {@link MethodAccessor}.
 */
public class AccessorException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new exception, which is typically thrown, when another exception
	 * was thrown inside the functions of the {@link Accessor},
	 * {@link FieldAccessor} or {@link MethodAccessor}.
	 * 
	 * @param message the exception message
	 */
	public AccessorException(String message) {
		super(message);
	}

	/**
	 * Creates a new exception, which is typically thrown, when another exception
	 * was thrown inside the functions of the {@link Accessor},
	 * {@link FieldAccessor} or {@link MethodAccessor}.
	 * 
	 * @param message the exception message
	 * @param cause the exception cause
	 */
	public AccessorException(String message, Exception cause) {
		super(message, cause);
	}
	
}
