package javautils.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javauitls.events.ClientConnectEvent;
import javauitls.events.ClientDisconnectEvent;
import javauitls.events.ClientDisconnectEvent.DisconnectCause;
import javauitls.events.ClientMessageRecieveEvent;
import javauitls.events.ClientMessageSendEvent;
import javautils.handler.EventHandler;

public class Client implements Runnable {

	private Socket socket;
	private Thread thread;
	private PrintStream output;
	private BufferedReader input;
	private MessageReceiveAction action;
	private boolean selfdisconnect;

	public Client(String host, int port) throws UnknownHostException, IOException {
		this(new Socket(host, port));
	}

	public Client(Socket socket) throws IOException {
		this.socket = socket;
		output = new PrintStream(socket.getOutputStream(), true);
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		action = (client, message) -> {};
		selfdisconnect = false;
		ClientConnectEvent event = new ClientConnectEvent(this);
		EventHandler.executeEvent(ClientConnectEvent.class, event);
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		String message;
		while (!thread.isInterrupted() && !isClosed()) {
			try {
				message = input.readLine();
				ClientMessageRecieveEvent event = new ClientMessageRecieveEvent(this, message);
				EventHandler.executeEvent(ClientMessageRecieveEvent.class, event);
				if (!event.isCancled()) {
					onMessageReceive(message);
				}
			} catch (SocketException socketException) {
				close();
				break;
			} catch (NullPointerException nullPointerException) {
				close();
				break;
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		DisconnectCause cause = DisconnectCause.CONNECTIONLOST;
		if (selfdisconnect) {
			cause = DisconnectCause.CONNECTTIONCLOSED;
		}
		ClientDisconnectEvent event = new ClientDisconnectEvent(this, cause);
		EventHandler.executeEvent(ClientDisconnectEvent.class, event);
	}

	public void sendMessage(String message) {
		ClientMessageSendEvent event = new ClientMessageSendEvent(this, message);
		EventHandler.executeEvent(ClientMessageSendEvent.class, event);
		if (!event.isCancled()) {
			output.println(message);
		}
	}
	
	public void onMessageReceive(String message) {
		action.onMessageReceive(this, message);
	}

	public void setOnMessageRecieve(MessageReceiveAction action) {
		this.action = action;
	}

	public void close() {
		try {
			socket.close();
			thread.interrupt();
			selfdisconnect = true;
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	public boolean isClosed() {
		return socket.isClosed();
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
