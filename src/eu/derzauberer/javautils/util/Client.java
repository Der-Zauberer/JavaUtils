package eu.derzauberer.javautils.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import eu.derzauberer.javautils.action.ClientMessageReceiveAction;
import eu.derzauberer.javautils.events.ClientConnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent;
import eu.derzauberer.javautils.events.ClientMessageRecieveEvent;
import eu.derzauberer.javautils.events.ClientMessageSendEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent.DisconnectCause;

public class Client implements Runnable {

	private Socket socket;
	private Server server;
	private Thread thread;
	private PrintStream output;
	private BufferedReader input;
	private ClientMessageReceiveAction action;
	private boolean selfdisconnect;

	public Client(String host, int port) throws UnknownHostException, IOException {
		this(new Socket(host, port));
	}

	public Client(Socket socket) throws IOException {
		this(socket, null);
	}
	
	public Client(Socket socket, Server server) throws IOException {
		this.server = server;
		this.socket = socket;
		output = new PrintStream(socket.getOutputStream(), true);
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		action = (event) -> {};
		selfdisconnect = false;
		new ClientConnectEvent(this);
		if (!isPartOfServer()) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {
		String message;
		while ((thread == null || !thread.isInterrupted()) && !isClosed()) {
			try {
				message = input.readLine();
				if (isPartOfServer() && server.isClosed()) {
					server.removeClientFromHandler(this);
					close();
					break;
				}
				ClientMessageRecieveEvent event = new ClientMessageRecieveEvent(this, message);
				if (!event.isCancelled()) {
					onMessageReceive(event);
				}
			} catch (SocketException socketException) {
				if (isPartOfServer()) server.removeClientFromHandler(this);
				close();
				break;
			} catch (NullPointerException nullPointerException) {
				if (isPartOfServer()) server.removeClientFromHandler(this);
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
		new ClientDisconnectEvent(this, cause);
	}

	public void sendMessage(String message) {
		ClientMessageSendEvent event = new ClientMessageSendEvent(this, message);
		if (!event.isCancelled()) {
			output.println(message);
		}
	}
	
	protected void onMessageReceive(ClientMessageRecieveEvent event) {
		if (isPartOfServer()) {
			server.onMessageRecieve(event);
		}
		action.onAction(event);
	}

	public void setOnMessageRecieve(ClientMessageReceiveAction action) {
		this.action = action;
	}

	public void close() {
		try {
			socket.close();
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
	
	public boolean isPartOfServer() {
		return server != null;
	}
	
	public Server getServer() {
		return server;
	}

}