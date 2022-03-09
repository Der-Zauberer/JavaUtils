package eu.derzauberer.javautils.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import eu.derzauberer.javautils.action.ClientMessageReceiveAction;
import eu.derzauberer.javautils.events.ClientConnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent.DisconnectCause;
import eu.derzauberer.javautils.events.ClientMessageReceiveEvent;
import eu.derzauberer.javautils.events.ClientMessageSendEvent;

public class Client implements Runnable {

	private Socket socket;
	private Server server;
	private Thread thread;
	private PrintStream output;
	private BufferedReader input;
	private ClientMessageReceiveAction action;
	private boolean disconnected;
	private DisconnectCause cause;

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
		disconnected = false;
		new ClientConnectEvent(this);
		if (!isPartOfServer()) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {
		String message;
		try {
			while (!isClosed()) {
				message = input.readLine();
				if (message.equals("null")) break;
				ClientMessageReceiveEvent event = new ClientMessageReceiveEvent(this, message);
				if (!event.isCancelled()) {
					onMessageReceive(event);
				}
			}
		} catch (SocketTimeoutException exception) {
			cause = DisconnectCause.TIMEOUT;
		} catch (SocketException | NullPointerException exception) {
			
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		if (cause == null) {
			cause = DisconnectCause.DISCONNECTED;
		}
		close();
	}

	public void sendMessage(String message) {
		ClientMessageSendEvent event = new ClientMessageSendEvent(this, message);
		if (!event.isCancelled()) {
			output.println(message);
		}
	}
	
	protected void onMessageReceive(ClientMessageReceiveEvent event) {
		if (isPartOfServer()) {
			server.onMessageReceive(event);
		}
		action.onAction(event);
	}

	public void setOnMessageReceive(ClientMessageReceiveAction action) {
		this.action = action;
	}
	
	public void setTimeout(int timeout) {
		try {
			socket.setSoTimeout(timeout);
		} catch (SocketException exception) {}
	}
	
	public int getTimeout() {
		try {
			return socket.getSoTimeout();
		} catch (SocketException exception) {
			return 0;
		}
	}
	
	public InetAddress getAdress() {
		return socket.getInetAddress();
	}
	
	public int getPort() {
		return socket.getPort();
	}
	
	public InetAddress getLocalAdress() {
		return socket.getLocalAddress();
	}
	
	public int getLocalPort() {
		return socket.getLocalPort();
	}

	public void close() {
		if (!disconnected) {
			disconnected = true;
			try {
				socket.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			if (cause == null) {
				cause = DisconnectCause.CLOSED;
			}
			new ClientDisconnectEvent(this, cause);
			if (isPartOfServer()) {
				server.getClients().remove(this);
			}
		}
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}
	
	public boolean isPartOfServer() {
		return server != null;
	}
	
	public Server getServer() {
		return server;
	}

}