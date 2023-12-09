package eu.derzauberer.javautils.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This interface contains the abstract methods {@link #parseIn(String)} and
 * {@link #parseOut()} for input and output.
 */
public interface Parsable<P extends Parsable<P>> {

	/**
	 * Parses the string into the parser object structure. This call will override
	 * the existing content of the parser!
	 * 
	 * @param input the input for the parser
	 * @return the own parser object for further customization
	 */
	public abstract P parseIn(String input);
	
	/**
	 * Reads a file and parse the file content in the {@link #parseIn(String)}.
	 * method. This call will override the existing content of the parser!
	 * 
	 * @param file the file to read
	 * @return the own parser object for further customization
	 * @throws SecurityException if java has no permission to write to the file
	 * @throws IOException       if an I/O exception occurs
	 */
	@SuppressWarnings("unchecked")
	public default P parseFromFile(Path file) throws IOException {
		parseIn(Files.readString(file));
		return (P) this;
	}
	
	/**
	 * Gets the output of the parser. The object structure will be
	 * converted back to a string.
	 * 
	 * @return the output of the parser
	 */
	public abstract String parseOut();
	
	/**
	 * Writes the parsed output to a file and create the file before writing if the
	 * file didn't exist. The method calls {@link #parseOut()} implicitly.
	 * 
	 * @param file the file to write
	 * @return the own parser object for further customization
	 * @throws SecurityException if java has no permission to write to the file
	 * @throws IOException       if an I/O exception occurs
	 */
	@SuppressWarnings("unchecked")
	public default P parseToFile(Path file) throws IOException {
		if (!Files.exists(file) && file.getParent() != null) {
			Files.createDirectories(file.getParent());
		}
		Files.writeString(file, parseOut());
		return (P) this;
	}

}
