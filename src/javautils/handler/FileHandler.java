package javautils.handler;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileHandler {

	public static File createFile(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		return file;
	}

	public static String readString(File file) {
		StringBuilder string = new StringBuilder();
		try {
			BufferedReader reader = Files.newBufferedReader(Paths.get(file.toString()), Charset.forName("UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				string.append(line + "\n");
			}
			reader.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return string.toString();
	}

	public static String[] readLines(File file) {
		return readString(file).split("\n");
	}
	
	public static void writeString(File file, String string) {
		writeLines(file, string.split("\n"), false);
	}

	public static void appendString(File file, String string) {
		writeLines(file, string.split("\n"), true);
	}
	
	public static void writeLines(File file, String lines[]) {
		writeLines(file, lines, false);
	}
		
	public static void appendLines(File file, String lines[]) {
		writeLines(file, lines, true);
	}

	public static void writeLines(File file, String lines[], boolean append) {
		try {
			BufferedWriter writer;
			if (append) {writer = Files.newBufferedWriter(Paths.get(file.toString()), Charset.forName("UTF-8"), StandardOpenOption.APPEND);}
			else {writer = Files.newBufferedWriter(Paths.get(file.toString()), Charset.forName("UTF-8"));}
			for (int i = 0; i < lines.length - 1; i++) {
				writer.write(lines[i]);
				writer.newLine();
			}
			writer.write(lines[lines.length - 1]);
			writer.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static void openFile(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static void openFileInBrowser(String url) {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (IOException | URISyntaxException exception) {
			exception.printStackTrace();
		}
	}

	public static File createDirectory(String path) {
		if (!path.startsWith("\\"))
			path = "\\" + path;
		File file = new File(getJarDirectory().toString() + path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static File getJarDirectory() {
		File file = null;
		try {
			file = new File(FileHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException exception) {}
		if (file.getName().endsWith(".jar")) {
			return file.getParentFile();
		}
		return file;
	}

}
