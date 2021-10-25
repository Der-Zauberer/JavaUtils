package javautils.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

	private Socket socket;
	private Thread thread;
	private PrintStream output;
	private BufferedReader input;
	private MessageReceiveAction action;

	public Client(String host, int port) throws UnknownHostException, IOException {
		this(new Socket(host, port));
	}

	public Client(Socket socket) throws IOException {
		this.socket = socket;
		output = new PrintStream(socket.getOutputStream(), true);
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		action = (client, message) -> {};
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		String message;
		while (!thread.isInterrupted() && socket.isConnected() && !socket.isClosed()) {
			try {
				while (input.ready()) {
					message = input.readLine();
					onMessageReceive(message);
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

	public void sendMessage(String message) {
		output.println(message);
	}
	
	public void onMessageReceive(String message) {
		action.onMessageReceive(this, message);
	}

	public void setOnMessageRecieve(MessageReceiveAction action) {
		this.action = action;
	}

	public Socket getSocket() {
		return socket;
	}

	public void close() {
		try {
			socket.close();
			thread.interrupt();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public void reopen(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		thread.start();
	}

	public void reopen(Socket socket) {
		this.socket = socket;
		thread.start();
	}

}
