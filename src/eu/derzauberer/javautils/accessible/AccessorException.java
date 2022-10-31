package eu.derzauberer.javautils.accessible;

/**
 * A exception, which is thrown, when another exception was thrown inside the
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
	 * @param string the exception message
	 */
	public AccessorException(String string) {
		super(string);
	}

}
