package eu.derzauberer.javautils.parser;

/**
 * The exception is thrown when a problem occurs in a parser.
 */
public class ParserException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception which is thrown when a problem occurs in a parser.
	 * 
	 * @param message the exception message
	 */
	public ParserException(String message) {
		super(message);
	}
	
	/**
	 * Creates a new exception which is thrown when a problem occurs in a parser.
	 * 
	 * @param message the exception message
	 * @param cause the exception cause
	 */
	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}

}
