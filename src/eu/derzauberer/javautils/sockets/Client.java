package eu.derzauberer.javautils.sockets;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import eu.derzauberer.javautils.action.ClientConnectAction;
import eu.derzauberer.javautils.action.ClientDisconnectAction;
import eu.derzauberer.javautils.action.ClientMessageReceiveAction;
import eu.derzauberer.javautils.action.ClientMessageSendAction;
import eu.derzauberer.javautils.events.ClientConnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent.DisconnectCause;
import eu.derzauberer.javautils.util.Sender;
import eu.derzauberer.javautils.events.ClientMessageReceiveEvent;
import eu.derzauberer.javautils.events.ClientMessageSendEvent;

public class Client implements Sender, Closeable {

	private Socket socket;
	private Server server;
	private Thread thread;
	private PrintStream output;
	private BufferedReader input;
	private ClientMessageReceiveAction clientMessageReceiveAction;
	private ClientMessageSendAction clientMessageSendAction;
	private ClientConnectAction clientConnectAction;
	private ClientDisconnectAction clientDisconnectAction;
	private boolean isDisconnected;
	private MessageType defaultMessageType;
	private DisconnectCause cause;

	public Client(String host, int port) throws UnknownHostException, IOException {
		this(new Socket(host, port));
	}

	public Client(Socket socket) throws IOException {
		this(socket, null);
	}
	
	protected Client(Socket socket, Server server) throws IOException {
		this.server = server;
		this.socket = socket;
		output = new PrintStream(socket.getOutputStream(), true);
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		isDisconnected = false;
		defaultMessageType = MessageType.DEFAULT;
		ClientConnectEvent event = new ClientConnectEvent(this);
		if (clientConnectAction != null) clientConnectAction.onAction(event);
		if (isPartOfServer() && server.getOnClientConnect() != null) server.getOnClientConnect().onAction(event);
		if (!isPartOfServer()) {
			thread = new Thread(this::inputLoop);
			thread.start();
		}
	}

	protected void inputLoop() {
		String message;
		try {
			while (!isClosed()) {
				message = input.readLine();
				if (message.equals("null")) break;
				sendInput(message);
			}
		} catch (SocketTimeoutException exception) {
			cause = DisconnectCause.TIMEOUT;
		} catch (SocketException | NullPointerException exception) {	
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		if (cause == null) cause = DisconnectCause.DISCONNECTED;
		try {
			close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public void sendInput(String string) {
		ClientMessageReceiveEvent event = new ClientMessageReceiveEvent(this, string);
		if (clientMessageReceiveAction != null && !event.isCancelled()) clientMessageReceiveAction.onAction(event);
		if (!event.isCancelled() && isPartOfServer() && server.getOnMessageReceive() != null) server.getOnMessageReceive().onAction(event);
	}
	
	@Override
	public void sendOutput(String string, MessageType type) {
		ClientMessageSendEvent event = new ClientMessageSendEvent(this, string);
		if (clientMessageSendAction != null && !event.isCancelled()) clientMessageSendAction.onAction(event);
		if (!event.isCancelled() && isPartOfServer() && server.getOnMessageSend() != null) server.getOnMessageSend().onAction(event);
		if (!event.isCancelled()) output.println(event.getMessage());
	}
	
	public Server getServer() {
		return server;
	}
	
	public boolean isPartOfServer() {
		return server != null;
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
	
	@Override
	public void close() throws IOException {
		if (!isDisconnected) {
			isDisconnected = true;
			socket.close();
			if (cause == null) cause = DisconnectCause.CLOSED;
		 	ClientDisconnectEvent event = new ClientDisconnectEvent(this, cause);
		 	if (clientDisconnectAction != null) clientDisconnectAction.onAction(event);
		 	if (isPartOfServer() && server.getOnClientDisconnect() != null) server.getOnClientDisconnect().onAction(event);
			if (isPartOfServer()) server.getClients().remove(this);
		}
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}
	
	public void setOnMessageReceive(ClientMessageReceiveAction action) {
		clientMessageReceiveAction = action;
	}
	
	public ClientMessageReceiveAction getOnMessageReceive() {
		return clientMessageReceiveAction;
	}
	
	public void setOnMessageSend(ClientMessageSendAction action) {
		clientMessageSendAction = action;
	}
	
	public ClientMessageSendAction getOnMessageSend() {
		return clientMessageSendAction;
	}
	
	public void setOnClientConnect(ClientConnectAction action) {
		clientConnectAction = action;
	}
	
	public ClientConnectAction getOnClientConnect() {
		return clientConnectAction;
	}
	
	public void setOnClientDisconnect(ClientDisconnectAction action) {
		clientDisconnectAction = action;
	}
	
	public ClientDisconnectAction getOnClientDisconnect() {
		return clientDisconnectAction;
	}
	
	public void setDefaultMessageType(MessageType defaultMessageType) {
		this.defaultMessageType = defaultMessageType;
	}
	
	@Override
	public MessageType getDefaultMessageType() {
		return defaultMessageType;
	}

}