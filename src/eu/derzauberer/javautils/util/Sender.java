package eu.derzauberer.javautils.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object that can send and receive messages.
 */
public interface Sender {

	/**
	 * Reads the amount of bytes and returns it as byte array. The method
	 * blocks until the length of the bytes where read, the end of the
	 * stream is reached or an exception occurred.
	 * 
	 * @param length the length of the byte array to read
	 * @return the byte array
	 * @throws IOException if an I/O exception occurs
	 */
	default byte[] readBytes(int length) throws IOException {
		final List<Byte> byteList = new ArrayList<>();
		byte[] bytes = new byte[1];
		int counter = 0;
		while (getInputStream().read(bytes) != -1 && counter < length) {
			byteList.add(bytes[0]);
			counter++;
		}
		byte[] bytesArray = new byte[byteList.size()];
		for (int i = 0; i < bytesArray.length; i++) {
			bytesArray[i] = byteList.get(i);
		}
		return bytesArray;
	}

	/**
	 * Reads until one of the seperator bytes is read and returns it as
	 * byte array. The method blocks until one of the seperator bytes is
	 * read, the end of the stream is reached or an exception occurred.
	 *
	 * @param seperator the seperator byte which is the end of the byte
	 *                  array
	 * @return the byte array
	 * @throws IOException if an I/O exception occurs
	 */
	default byte[] readBytes(byte[] seperator) throws IOException {
		final List<Byte> byteList = new ArrayList<>();
		byte[] bytes = new byte[1];
		loop:
		while (getInputStream().read(bytes) != -1) {
			for (int i = 0; i < seperator.length; i++) {
				if (seperator[i] == 10 && bytes[0] == 13) {
					setNextLineIgnore(true);
					break loop;
				} else if (seperator[i] == 10 && bytes[0] == 10 && isNextLineIgnored()) {
					setNextLineIgnore(false);
					continue loop;
				} else if (bytes[0] == seperator[i]) {
					break loop;
				}
			}
			setNextLineIgnore(false);
			byteList.add(bytes[0]);
		}
		byte[] bytesArray = new byte[byteList.size()];
		for (int i = 0; i < bytesArray.length; i++) {
			bytesArray[i] = byteList.get(i);
		}
		return bytesArray;
	}
	
	/**
	 * Reads the amount of bytes or until one of the seperator bytes is
	 * read and returns it as byte array. The method blocks until the
	 * length of the bytes where read, one of the seperator bytes is read,
	 * the end of the stream is reached or an exception occurred.
	 *
	 * @param length    the length of the byte array to read
	 * @param seperator the seperator byte which is the end of the byte
	 *                  array
	 * @return the byte array
	 * @throws IOException if an I/O exception occurs
	 */
	default byte[] readBytes(int length, byte[] seperator) throws IOException {
		final List<Byte> byteList = new ArrayList<>();
		byte[] bytes = new byte[1];
		int counter = 0;
		loop:
		while (getInputStream().read(bytes) != -1 && counter < length) {
			for (int i = 0; i < seperator.length; i++) {
				if (seperator[i] == 10 && bytes[0] == 13) {
					setNextLineIgnore(true);
					break loop;
				} else if (seperator[i] == 10 && bytes[0] == 10 && isNextLineIgnored()) {
					setNextLineIgnore(false);
					continue loop;
				} else if (bytes[0] == seperator[i]) {
					break loop;
				}
			}
			setNextLineIgnore(false);
			byteList.add(bytes[0]);
		}
		byte[] bytesArray = new byte[byteList.size()];
		for (int i = 0; i < bytesArray.length; i++) {
			bytesArray[i] = byteList.get(i);
		}
		return bytesArray;
	}

	/**
	 * Blocks until a line break is recognized and returns the byte array as string.
	 * The bytes are converted with the {@link Charset} to a string.
	 * 
	 * @return the line as string
	 * @throws IOException if an I/O exception occurs
	 */
	default String readLine() throws IOException {
		final byte[] seperator = {10};
		return new String(readBytes(seperator), getCharset());
	}

	/**
	 * Blocks until all lines are read and no one are remaining and returns the byte
	 * array as string. The bytes are converted with the {@link Charset} to a
	 * string.
	 * 
	 * @return the line as string
	 * @throws IOException if an I/O exception occurs
	 */
	default String readAll() throws IOException {
		final StringBuilder string = new StringBuilder();
		String line;
		while ((line = readLine()) != null) string.append(line);
		return string.toString();
	}
	
	/**
	 * Sends a byte array to the stream. It does nothing if the stream is already
	 * closed.
	 * 
	 * @param bytes the byte array to send
	 */
	default void sendBytes(byte[] bytes) {
		try {
			getOutputStream().write(bytes);
		} catch (IOException exception) {}
	}

	/**
	 * Sends a string to the stream. It does nothing if the stream is already
	 * closed.
	 * 
	 * @param string the string to send to the stream
	 */
	default void send(String string) {
		sendBytes(string.getBytes(getCharset()));
	}

	/**
	 * Sends a formatted string with object arguments by they {@link #toString()}
	 * method to the stream. The arguments use the
	 * {@link String#format(String, Object...)} method. After processing the string
	 * the function calls the {@link #send(String)} method. It does nothing if the
	 * stream is already closed.
	 * 
	 * @param string the formatted string to send to the stream
	 * @param args   the arguments that are passed in the string
	 */
	default void send(String string, Object... args) {
		send(String.format(string, args));
	}

	/**
	 * Sends an object by it's {@link #toString()} method to the stream. After
	 * processing the string the function calls the {@link #send(String)} method. It
	 * does nothing if the stream is already closed.
	 * 
	 * @param object the object to send to the stream
	 */
	default void send(Object object) {
		send(object.toString());
	}

	/**
	 * Sends a string to the stream with a new line at the end. After processing the
	 * string the function calls the {@link #send(String)} method. It does nothing
	 * if the stream is already closed.
	 * 
	 * @param string the string to send to the stream
	 */
	default void sendLine(String string) {
		send(string + "\n");
	}

	/**
	 * Sends a formatted string with object arguments by they {@link #toString()}
	 * method to the stream with a new line at the end. The arguments use the
	 * {@link String#format(String, Object...)} method. After processing the string
	 * the function calls the {@link #send(String)} method. It does nothing if the
	 * stream is already closed.
	 * 
	 * @param string the formatted string to send to the stream
	 * @param args   the arguments that are passed in the string
	 */
	default void sendLine(String string, Object... args) {
		send(String.format(string, args) + "\n");
	}

	/**
	 * Sends an object by it's {@link #toString()} method to the stream with a new
	 * line at the end. After processing the string the function calls the
	 * {@link #send(String)} method. It does nothing if the stream is already
	 * closed.
	 * 
	 * @param object the object to send to the stream
	 */
	default void sendLine(Object object) {
		send(object.toString() + "\n");
	}
	
	/**
	 * Returns the input stream of the sender.
	 * 
	 * @return the input stream
	 */
	InputStream getInputStream();
	
	/**
	 * Returns the output stream of the sender.
	 * 
	 * @return the output stream
	 */
	OutputStream getOutputStream();

	/**
	 * Returns the charset for the stream that is in use when converting bytes to
	 * strings.
	 * 
	 * @return the charset for the stream
	 */
	Charset getCharset();
	
	/**
	 * Sets a memory bit which is required to process line breakes.
	 * 
	 * @param nextLine a memory bit
	 */
	void setNextLineIgnore(boolean nextLineIgnored);
	
	/**
	 * Returns a memory bit which is required to process line breakes.
	 * 
	 * @return a memory bit
	 */
	boolean isNextLineIgnored();

}
