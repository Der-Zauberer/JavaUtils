package eu.derzauberer.javautils.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Represents an object that can send and receive messages.
 */
public interface Sender {

	/**
	 * Reads the an amount of bytes and returns the byte array. The method
	 * blocks until the length of the bytes where read, the end of the
	 * stream is reached or an exception occurred. The function only calls
	 * the {@link InputStream#readNBytes(int)} method.
	 * 
	 * @param lenght the length of the byte array to read
	 * @return the byte array
	 * @throws IOException if an I/O exception occurs
	 * @see {@link InputStream#readNBytes(int)}
	 */
	byte[] readBytes(int lenght) throws IOException;

	/**
	 * Reads the an amount of bytes and returns the byte array. The method
	 * blocks until the end of the stream is reached or an exception
	 * occurred. The function only calls the
	 * {@link InputStream#read(byte[])} method.
	 * 
	 * @param bytes the byte array
	 * @return the number of bytes that where read
	 * @throws IOException if an I/O exception occurs
	 * @see {@link InputStream#read(byte[])}
	 */
	int readBytes(byte[] bytes) throws IOException;
	
	/**
	 * Blocks until a line break is recognized and returns the byte array
	 * as string. The bytes are converted with the {@link Charset} to a
	 * string.
	 * 
	 * @return the line as string
	 * @throws IOException if an I/O exception occurs
	 */
	String readLine() throws IOException;

	/**
	 * Blocks until all lines are read and no one are remaining and returns the byte array
	 * as string. The bytes are converted with the {@link Charset} to a
	 * string.
	 * 
	 * @return the line as string
	 * @throws IOException if an I/O exception occurs
	 */
	default String readAll() throws IOException {
		final StringBuilder string = new StringBuilder();
		String line;
		while ((line = readLine()) == null) string.append(line);
		return string.toString();
	}
	
	/**
	 * Sends an byte array to the stream. It does nothing if the stream is
	 * already closed.
	 * 
	 * @param bytes the byte array to send
	 */
	void sendBytes(byte[] bytes);

	/**
	 * Sends a string to the stream. It does nothing if the stream is
	 * already closed.
	 * 
	 * @param string the string to send to the stream
	 */
	default void send(String string) {
		sendBytes(string.getBytes(getCharset()));
	}

	/**
	 * Sends a formatted string with object arguments by they
	 * {@link #toString()} method to the stream. The arguments uses the
	 * {@link String#format(String, Object...)} method. After processing
	 * the string the function calls the {@link #send(String)} method. It
	 * does nothing if the stream is already closed.
	 * 
	 * @param string the formatted string to send to the stream
	 * @param args   the arguments that are passed in the string
	 */
	default void send(String string, Object... args) {
		send(String.format(string, args));
	}

	/**
	 * Sends an object by it's {@link #toString()} method to the stream.
	 * After processing the string the function calls the
	 * {@link #send(String)} method. It does nothing if the stream is
	 * already closed.
	 * 
	 * @param object the object to send to the stream
	 */
	default void send(Object object) {
		send(object.toString());
	}

	/**
	 * Sends a string to the stream with a new line at the end. After
	 * processing the string the function calls the {@link #send(String)}
	 * method. It does nothing if the stream is already closed.
	 * 
	 * @param string the string to send to the stream
	 */
	default void sendLine(String string) {
		send(string + "\n");
	}

	/**
	 * Sends a formatted string with object arguments by they
	 * {@link #toString()} method to the stream with a new line at the
	 * end. The arguments uses the
	 * {@link String#format(String, Object...)} method. After processing
	 * the string the function calls the {@link #send(String)} method. It
	 * does nothing if the stream is already closed.
	 * 
	 * @param string the formatted string to send to the stream
	 * @param args   the arguments that are passed in the string
	 */
	default void sendLine(String string, Object... args) {
		send(String.format(string, args) + "\n");
	}

	/**
	 * Sends an object by it's {@link #toString()} method to the stream
	 * with a new line at the end. After processing the string the
	 * function calls the {@link #send(String)} method. It does nothing if
	 * the stream is already closed.
	 * 
	 * @param object the object to send to the stream
	 */
	default void sendLine(Object object) {
		send(object.toString() + "\n");
	}

	/**
	 * Returns the charset for the streams that is in use when converting
	 * bytes to strings.
	 * 
	 * @return the charset for the streams
	 */
	Charset getCharset();

}
