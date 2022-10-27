package eu.derzauberer.javautils.parser;

/**
 * The <tt>Parser</tt> interface contains the abstract methods
 * <tt>parse(String input)</tt> and <tt>toString()</tt> for input and output.
 */
public interface Parser {

	/**
	 * The parse method is the input for the parser, the string will be parsed to
	 * the object structure of the parser in this method.
	 * @param input the input for the parser
	 */
	public abstract void parseIn(final String input);

	/**
	 * This is the output method of the parser. The object structure will be
	 * converted back to a string.
	 * @return the output of the parser
	 */
	public abstract String parseOut();

}
