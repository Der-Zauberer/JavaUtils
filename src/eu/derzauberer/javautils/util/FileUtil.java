package eu.derzauberer.javautils.util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * The class provides basic functions to deal with files, for example copy,
 * delete, read, write, download, ...
 */
public class FileUtil {

	/**
	 * Creates a file and the folders if necessary. The method does nothing if the
	 * file already exists.
	 * 
	 * @param file The file, which should be created
	 * @throws IOException       if an I/O exception occurs
	 * @throws SecurityException if java has no permission to create the file or
	 *                           folder
	 */
	public static void createFile(File file) throws IOException {
		if (!file.exists()) {
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
	}

	/**
	 * Deletes a file of folder. The method does nothing if the file doesn't exist.
	 * 
	 * @param file The file or folder, which should be deleted
	 * @throws IOException       if an I/O exception occurs
	 * @throws SecurityException if java has no permission to create the file or
	 *                           folder
	 */
	public static void deleteFile(File file) throws IOException {
		if (file.exists()) {
			final Stream<Path> stream = Files.walk(Paths.get(file.toURI()));
			stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			stream.close();
		}
	}

	/**
	 * Copies the file or folder to another location
	 * 
	 * @param original the original file or folder, which should be copied
	 * @param copy     the new directory to copy the files to
	 * @throws IOException       if an I/O exception occurs
	 * @throws SecurityException if java has no permission to copy the file or
	 *                           folder
	 */
	public static void copyDirectory(File original, File copy) throws IOException {
		if (original.isDirectory()) {
			if (!copy.exists()) {
				copy.mkdir();
			}
			final String[] children = original.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(original, children[i]), new File(copy, children[i]));
			}
		} else {
			final InputStream input = new FileInputStream(original);
			final OutputStream output = new FileOutputStream(copy);
			final byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = input.read(bytes)) > 0) {
				output.write(bytes, 0, length);
			}
			input.close();
			output.close();
		}
	}

	/**
	 * Reads a file and returns the content as string.
	 * 
	 * @param file the file to read
	 * @return the content of the file as string
	 * @throws IOException       if an I/O exception occurs
	 * @throws SecurityException if java has no permission to read the file
	 */
	public static String readString(File file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file.toURI())));
	}
	
	/**
	 * Reads a file and returns the content as byte array.
	 * 
	 * @param file the file to read
	 * @return the content of the file as byte array
	 * @throws IOException       if an I/O exception occurs
	 * @throws SecurityException if java has no permission to read the file
	 */
	public static byte[] readBytes(File file) throws IOException {
		return Files.readAllBytes(Paths.get(file.toURI()));
	}

	/**
	 * Writes a string to a file and create the file before writing if the file
	 * didn't exist.
	 * 
	 * @param file   the file to write
	 * @param string the string to write to the file
	 * @throws SecurityException if java has no permission to write to the file
	 * @throws IOException       if an I/O exception occurs
	 */
	public static void writeString(File file, String string) throws IOException {
		createFile(file);
		Files.write(Paths.get(file.toURI()), string.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	/**
	 * Writes an byte array to a file and create the file before writing if the file
	 * didn't exist.
	 * 
	 * @param file  the file to write
	 * @param bytes the byte array to write to the file
	 * @throws SecurityException if java has no permission to write to the file
	 * @throws IOException       if an I/O exception occurs
	 */
	public static void writeString(File file, byte[] bytes) throws IOException {
		createFile(file);
		Files.write(Paths.get(file.toURI()), bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * Appends a string to a file and create the file before writing if the file
	 * didn't exist.
	 * 
	 * @param file   the file to append
	 * @param string the string to write to the file
	 * @throws SecurityException if java has no permission to append to the file
	 * @throws IOException       if an I/O exception occurs
	 */
	public static void appendString(File file, String string) throws IOException {
		createFile(file);
		Files.write(Paths.get(file.toURI()), string.getBytes(), StandardOpenOption.APPEND);
	}
	
	/**
	 * Appends an byte array to a file and create the file before writing if the
	 * file didn't exist.
	 * 
	 * @param file  the file to append
	 * @param bytes the byte array to write to the file
	 * @throws SecurityException if java has no permission to append to the file
	 * @throws IOException       if an I/O exception occurs
	 */
	public static void appendString(File file, byte[] bytes) throws IOException {
		createFile(file);
		Files.write(Paths.get(file.toURI()), bytes, StandardOpenOption.APPEND);
	}
	
	/**
	 * Connects to a website and returns it's content as string.
	 * 
	 * @param url the url of the website
	 * @return the content of the website as string
	 * @throws IOException if an I/O exception occurs
	 */
	public static String getStringFromWebsite(URL url) throws IOException {
		return getStringFromWebsite(url, false);
	}
	
	/**
	 * Connects to a website and returns it's content as string.
	 * 
	 * @param url            the url of the website
	 * @param removeHtmlTags if the returned string should not contain html tags
	 * @return the content of the website as string
	 * @throws IOException if an I/O exception occurs
	 */
	public static String getStringFromWebsite(URL url, boolean removeHtmlTags) throws IOException {
		final Scanner scanner = new Scanner(url.openStream());
		final StringBuilder builder = new StringBuilder();
		while (scanner.hasNext()) {
			builder.append(scanner.nextLine());
			builder.append("\n");
		}
		String string = builder.toString().substring(0, builder.length() - 1);
		if (removeHtmlTags) {
			string = string.replaceAll("<[^>]*>", "");
		}
		scanner.close();
		return string;
	}

	/**
	 * Downloads a file from a website and saves it in the file system A new file
	 * will be created if it didn't exist.
	 * 
	 * @param url  the url of the website
	 * @param file the file, which represents the download file
	 * @throws SecurityException if java has no permission write to a file or
	 *                           create a new one
	 * @throws IOException       if an I/O exception occurs
	 */
	public static void downloadFileFromWebsite(URL url, File file) throws IOException {
		final ReadableByteChannel readChannel = Channels.newChannel(url.openStream());
		final FileOutputStream output = new FileOutputStream(file.getPath());
		output.getChannel().transferFrom(readChannel, 0, Long.MAX_VALUE);
	}

	/**
	 * Opens the file in the operating systems default application.
	 * 
	 * @param file the file to open
	 * @throws SecurityException if java has no permission to open the file
	 * @throws IOException       if an I/O exception occurs
	 */
	public static void openFile(File file) throws IOException {
		Desktop.getDesktop().open(file);
	}
	
	/**
	 * Opens the file in the operating systems default browser.
	 * 
	 * @param file the file to open
	 * @throws SecurityException if java has no permission to open the file
	 * @throws IOException       if an I/O exception occurs
	 */
	public static void openFileInBrowser(File file) throws IOException {
		Desktop.getDesktop().browse(file.toURI());
	}

	/**
	 * Opens the file in the operating systems default file browser.
	 * 
	 * @param file the file to open
	 * @throws SecurityException if java has no permission to open the file
	 * @throws IOException       if an I/O exception occurs
	 */
	public static void openFileDirectory(File file) {
		Desktop.getDesktop().browseFileDirectory(file);
	}
	
	/**
	 * Opens the url in the operating systems default file browser.
	 * 
	 * @param url the url to open
	 * @throws URISyntaxException if this URL is not formatted strictly according
	 *                            to RFC2396 and cannot be converted to a URI
	 * @throws IOException        if an I/O exception occurs
	 */
	public static void openUrlInBrowser(URL url) throws IOException, URISyntaxException {
		Desktop.getDesktop().browse(url.toURI());
	}
	
	/**
	 * Opens moves the file in the operating system default trash directory.
	 * 
	 * @param file the file to open
	 * @return if the operating was successful
	 * @throws SecurityException if java has no permission to move the file to
	 *                           trash
	 * @throws IOException       if an I/O exception occurs
	 */
	public static boolean moveFileToTrash(File file) {
		return Desktop.getDesktop().moveToTrash(file);
	}
	
	/**
	 * Returns the operating system default home directory of the current user as
	 * {@link File}.
	 * 
	 * @return the operating system default home directory of the current user as
	 *         file
	 */
	public static File getHomeDirectory() {
		return new File(System.getProperty("user.home"));
	}
	
	/**
	 * Returns the operating system default temp directory as file.
	 * 
	 * @return the operating system default temp directory as file
	 */
	public static File getTempDirectory() {
		return new File(System.getProperty("java.io.tmpdir"));
	}
	
	/**
	 * Returns the directory in which the jar file is executed in this moment as
	 * {@link File}.
	 * 
	 * @return the directory in which the jar file is executed in this moment as
	 *         file
	 */
	public static File getExecutionDirectory() {
		return new File(System.getProperty("user.dir"));
	}

}