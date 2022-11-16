package eu.derzauberer.javautils.parser;

/**
 * The exception is thrown, when a problem occurs while trying to serialize
 * objects for a {@link Parser}.
 *
 */
public class SerializationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
	 *
	 * Creates an exception, which is thrown, when a problem occurs while trying to
	 * serialize objects with the {@link Access}.
	 *
	 * @param message the exception message
	 */
	public SerializationException(String message) {
		super(message);
	}

	/**
	 *
	 * Creates an exception, which is thrown, when a problem occurs while trying to
	 * serialize objects with the {@link Access}.
	 *
	 * @param message the exception message
	 * @param cause the exception cause
	 */
	public SerializationException(String message, Exception cause) {
		super(message, cause);
	}

}
