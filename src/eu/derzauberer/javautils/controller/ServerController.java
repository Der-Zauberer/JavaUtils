package eu.derzauberer.javautils.controller;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import eu.derzauberer.javautils.events.ClientConnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent;
import eu.derzauberer.javautils.events.ClientMessageReceiveEvent;
import eu.derzauberer.javautils.events.ClientMessageSendEvent;
import eu.derzauberer.javautils.util.Sender;

/**
 * This server socket can be used to send and receive messages from
 * multiple client sockets, for example the {@link ClientController}.
 */
public class ServerController implements Sender, Closeable {
	
	private final ServerSocket server;
	private final ExecutorService service;
	private int clientTimeout;
	private MessageType defaultMessageType;
	private Consumer<ClientMessageReceiveEvent> messageReceiveAction;
	private Consumer<ClientMessageSendEvent> messageSendAction;
	private Consumer<ClientConnectEvent> connectAction;
	private Consumer<ClientDisconnectEvent> disconnectAction;
	private List<ClientController> clients;
	
	/**
	 * Creates a new server socket based on a port.
	 * 
	 * @param port the port on which the server should run
	 * @throws IOException if an I/O exception occurs
	 */
	public ServerController(int port) throws IOException {
		this(new ServerSocket(port));
	}
	
	/**
	 * Creates a new server socket based on an existing server socket.
	 * 
	 * @param server the existing server socket
	 * @throws IOException if an I/O exception occurs
	 */
	public ServerController(ServerSocket server) throws IOException {
		this.server = server;
		clients = new ArrayList<>();
		service = Executors.newCachedThreadPool();
		final Thread thread = new Thread(this::inputLoop);
		clientTimeout = 0;
		defaultMessageType = MessageType.DEFAULT;
		thread.start();
	}
	
	/**
	 * Waits for incoming messages from the client sockets.
	 */
	private void inputLoop() {
		try {
			while (!server.isClosed()) {
				try {
					final Socket socket = server.accept();
					final ClientController client = new ClientController(socket, this);
					client.setTimeout(clientTimeout);
					service.submit(client::inputLoop);
					clients.add(client);
				} catch (RejectedExecutionException exception) {}
			}
		} catch (SocketTimeoutException | SocketException exception) {
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		if (!server.isClosed()) {
			try {
				close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	/**
	 * Does nothing, the input must be related to a client!
	 * 
	 * @throws UnsupportedOperationException the input must be related to a client
	 */
	@Override
	public void sendInput(String input) {
		throw new UnsupportedOperationException("The input must be related to a client");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendOutput(String message, MessageType type) {
		for (ClientController client : clients) {
			client.sendOutput(message, type);
		}
	}
	
	/**
	 * Returns the address of the server
	 * 
	 * @return the address of the server
	 */
	public InetAddress getAddress() {
		return server.getInetAddress();
	}
	
	/**
	 * Returns the local port of the server
	 * 
	 * @return the local port of the server
	 */
	public int getLocalPort() {
		return server.getLocalPort();
	}
	
	/**
	 * Sets the timeout of the server. If the server doesn't receive a message from
	 * any client during this time the connection will be closed.
	 * 
	 * @param timeout the timeout of the client in milliseconds
	 */
	public void setServerTimeout(int timeout) {
		try {
			server.setSoTimeout(timeout);
		} catch (SocketException exception) {
		}
	}

	/**
	 * Returns the timeout of the server. If the server doesn't receive a message
	 * from any client during this time the connection will be closed.
	 * 
	 * @return the timeout of the client in milliseconds
	 */
	public int getServerTimeout() {
		try {
			return server.getSoTimeout();
		} catch (IOException exception) {
			return 0;
		}
	}

	/**
	 * Sets the timeout of the client. If the server doesn't receive a message from
	 * the client during this time the connection will be closed.
	 * 
	 * @param timeout the timeout of the client in milliseconds
	 */
	public void setClientTimeout(int timeout) {
		clientTimeout = timeout;
		for (ClientController client : getClients()) {
			if (!client.isClosed())
				client.setTimeout(timeout);
		}
	}

	/**
	 * Returns the timeout of the client. If the server doesn't receive a message
	 * from the client during this time the connection will be closed.
	 * 
	 * @return the timeout of the client in milliseconds
	 */
	public int getClientTimeout() {
		return clientTimeout;
	}
	
	/**
	 * Closes the connections between clients and server.
	 * 
	 * @throws IOException if an I/O exception occurs
	 */
	@Override
	public void close() throws IOException {
		if (!server.isClosed()) {
			server.close();
			for (int i = 0; i < clients.size(); i++) {
				clients.get(i).close();
			}
			service.shutdown();
		}
	}
	
	/**
	 * Returns if the connections between clients and server are closed.
	 * 
	 * @return if the connections between clients and server are closed
	 */
	public boolean isClosed() {
		return server.isClosed();
	}

	/**
	 * Returns an unmodifiable list of all clients currently connected to this
	 * server.
	 * 
	 * @return an unmodifiable list of all clients currently connected to this
	 *         server
	 */
	public List<ClientController> getClients() {
		return Collections.unmodifiableList(clients);
	}

	/**
	 * Sets an action to execute when the socket receives a message.
	 * 
	 * @param messageReceiveAction an action, which is executed when the socket
	 *                             receives a message
	 */
	public void setMessageReceiveAction(Consumer<ClientMessageReceiveEvent> messageReceiveAction) {
		this.messageReceiveAction = messageReceiveAction;
	}

	/**
	 * Returns an action to execute when the socket receives a message.
	 * 
	 * @return an action to execute when the socket receives a message
	 */
	public Consumer<ClientMessageReceiveEvent> getMessageReceiveAction() {
		return messageReceiveAction;
	}

	/**
	 * Sets an action to execute when the socket sends a message.
	 * 
	 * @param messageReceiveAction an action to execute when the socket sends a
	 *                             message
	 */
	public void setMessageSendAction(Consumer<ClientMessageSendEvent> messageSendAction) {
		this.messageSendAction = messageSendAction;
	}

	/**
	 * Returns an action to execute when the socket sends a message.
	 * 
	 * @return an action to execute when the socket sends a message
	 */
	public Consumer<ClientMessageSendEvent> getMessageSendAction() {
		return messageSendAction;
	}

	/**
	 * Sets an action to execute when the socket connected to a server.
	 * 
	 * @param messageReceiveAction an action to be executed when the socket
	 *                             connected to a server
	 */
	public void setConnectAction(Consumer<ClientConnectEvent> connectAction) {
		this.connectAction = connectAction;
	}

	/**
	 * Returns an action to execute when the socket connected to a server.
	 * 
	 * @return an action to execute when the socket connected to a server
	 */
	public Consumer<ClientConnectEvent> getConnectAction() {
		return connectAction;
	}

	/**
	 * Sets an action to execute when the socket disconnected to a server.
	 * 
	 * @param messageReceiveAction an action to execute when the socket disconnected
	 *                             to a server
	 */
	public void setDisconnectAction(Consumer<ClientDisconnectEvent> disconnectAction) {
		this.disconnectAction = disconnectAction;
	}

	/**
	 * Returns an action to execute when the socket disconnected to a server.
	 * 
	 * @return an action to execute when the socket disconnected to a server
	 */
	public Consumer<ClientDisconnectEvent> getDisconnectAction() {
		return disconnectAction;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDefaultMessageType(MessageType type) {
		defaultMessageType = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MessageType getDefaultMessageType() {
		return defaultMessageType;
	}

}