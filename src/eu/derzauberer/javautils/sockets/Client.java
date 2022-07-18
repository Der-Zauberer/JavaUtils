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
import java.util.function.Consumer;
import eu.derzauberer.javautils.events.ClientConnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent.DisconnectCause;
import eu.derzauberer.javautils.events.ClientMessageReceiveEvent;
import eu.derzauberer.javautils.events.ClientMessageSendEvent;
import eu.derzauberer.javautils.util.Sender;

public class Client implements Sender, Closeable {

	private Socket socket;
	private Server server;
	private Thread thread;
	private PrintStream output;
	private BufferedReader input;
	private Consumer<ClientMessageReceiveEvent> messageReceiveAction;
	private Consumer<ClientMessageSendEvent> messageSendAction;
	private Consumer<ClientConnectEvent> connectAction;
	private Consumer<ClientDisconnectEvent> disconnectAction;
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
		final ClientConnectEvent event = new ClientConnectEvent(this);
		if (connectAction != null) connectAction.accept(event);
		if (isPartOfServer() && server.getConnectAction() != null) server.getConnectAction().accept(event);
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
	public void sendInput(String input) {
		final ClientMessageReceiveEvent event = new ClientMessageReceiveEvent(this, input);
		if (messageReceiveAction != null && !event.isCancelled()) messageReceiveAction.accept(event);
		if (!event.isCancelled() && isPartOfServer() && server.getMessageReceiveAction() != null) server.getMessageReceiveAction().accept(event);
	}
	
	@Override
	public void sendOutput(String message, MessageType type) {
		final ClientMessageSendEvent event = new ClientMessageSendEvent(this, message);
		if (messageSendAction != null && !event.isCancelled()) messageSendAction.accept(event);
		if (!event.isCancelled() && isPartOfServer() && server.getMessageSendAction() != null) server.getMessageSendAction().accept(event);
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
			final ClientDisconnectEvent event = new ClientDisconnectEvent(this, cause);
		 	if (disconnectAction != null) disconnectAction.accept(event);
		 	if (isPartOfServer() && server.getDisconnectAction() != null) server.getDisconnectAction().accept(event);
			if (isPartOfServer()) server.getClients().remove(this);
		}
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}
	
	public void setMessageReceiveAction(Consumer<ClientMessageReceiveEvent> messageReceiveAction) {
		this.messageReceiveAction = messageReceiveAction;
	}
	
	public Consumer<ClientMessageReceiveEvent> getMessageReceiveAction() {
		return messageReceiveAction;
	}
	
	public void setMessageSendAction(Consumer<ClientMessageSendEvent> messageSendAction) {
		this.messageSendAction = messageSendAction;
	}
	
	public Consumer<ClientMessageSendEvent> getMessageSendAction() {
		return messageSendAction;
	}
	
	public void setConnectAction(Consumer<ClientConnectEvent> connectAction) {
		this.connectAction = connectAction;
	}
	
	public Consumer<ClientConnectEvent> getConnectAction() {
		return connectAction;
	}
	
	public void setDisconnectAction(Consumer<ClientDisconnectEvent> disconnectAction) {
		this.disconnectAction = disconnectAction;
	}
	
	public Consumer<ClientDisconnectEvent> getDisconnectAction() {
		return disconnectAction;
	}
	
	@Override
	public void setDefaultMessageType(MessageType defaultMessageType) {
		this.defaultMessageType = defaultMessageType;
	}
	
	@Override
	public MessageType getDefaultMessageType() {
		return defaultMessageType;
	}

}