package eu.derzauberer.javautils.util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FileUtil {

	private static final ArrayList<FileObserver> fileObserver = new ArrayList<>();
	private static Timer timer;

	public static void createFile(File file) {
		if (!file.exists()) {
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException exception) {
				throw new UncheckedIOException(exception);
			}
		}
	}

	public static void deleteFile(File file) {
		if (file.exists()) {
			try {
				final Stream<Path> stream = Files.walk(Paths.get(file.toURI()));
				stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
				stream.close();
			} catch (IOException exception) {
				throw new UncheckedIOException(exception);
			}
		}
	}

	public static void copyDirectory(File original, File copy) {
		try {
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
				final byte[] buffer = new byte[1024];
				int lennght = 0;
				while ((lennght = input.read(buffer)) > 0) {
					output.write(buffer, 0, lennght);
				}
				input.close();
				output.close();
			}
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	public static String readString(File file) {
		String string = "";
		try {
			string = new String(Files.readAllBytes(Paths.get(file.toURI())));
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
		return string;
	}

	public static void writeString(File file, String string) {
		createFile(file);
		try {
			Files.write(Paths.get(file.toURI()), string.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	public static void appendString(File file, String string) {
		createFile(file);
		try {
			Files.write(Paths.get(file.toURI()), string.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	public static String getStringFromWebsite(URL url) throws IOException {
		return getStringFromWebsite(url, false);
	}

	public static String getStringFromWebsite(URL url, boolean removeHtmlTags) {
		String string = "";
		try {
			final Scanner scanner = new Scanner(url.openStream());
			final StringBuilder builder = new StringBuilder();
			while (scanner.hasNext()) {
				builder.append(scanner.nextLine());
				builder.append("\n");
			}
			string = builder.toString().substring(0, builder.length() - 1);
			if (removeHtmlTags) {
				string = string.replaceAll("<[^>]*>", "");
			}
			scanner.close();
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
		return string;
	}

	public static void downloadFileFromWebsite(URL url, File file) {
		try {
			final ReadableByteChannel readChannel = Channels.newChannel(url.openStream());
			final FileOutputStream output = new FileOutputStream(file.getPath());
			output.getChannel().transferFrom(readChannel, 0, Long.MAX_VALUE);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	public static void openFile(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	public static void setOnFileUpdated(File file, Consumer<File> action) {
		if (timer == null) timer = new Timer();
		fileObserver.add(new FileUtil().new FileObserver(file, action));
	}

	public static void removeFileFromUpdateObserver(File file) {
		for (int i = 0; i < fileObserver.size(); i++) {
			if (file == fileObserver.get(i).file) {
				fileObserver.get(i).cancel();
				fileObserver.remove(i);
				if (fileObserver.isEmpty()) {
					timer.cancel();
					timer = null;
				}
				return;
			}
		}
	}

	public static void openFileInBrowser(String url) {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		} catch (URISyntaxException exception) {
			exception.printStackTrace();
		}
	}

	public static File getJarFile() {
		File file = null;
		try {
			file = new File(FileUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException exception) {
			exception.printStackTrace();
		}
		if (file.getName().endsWith(".jar")) return file;
		return file;
	}

	public static File getJarDirectory() {
		File file = null;
		try {
			file = new File(FileUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException exception) {
			exception.printStackTrace();
		}
		if (file.getName().endsWith(".jar")) return file.getParentFile();
		return file;
	}

	private class FileObserver extends TimerTask {

		private File file;
		private Consumer<File> action;
		private long timestamp;

		public FileObserver(File file, Consumer<File> action) {
			this.file = file;
			this.action = action;
			this.timestamp = file.lastModified();
			FileUtil.timer.schedule(this, 0, 700);
		}

		@Override
		public void run() {
			if (!file.exists()) {
				FileUtil.removeFileFromUpdateObserver(file);
			} else if (file.lastModified() != timestamp) {
				action.accept(file);
				timestamp = file.lastModified();
			}
		}
		
	}

}