package eu.derzauberer.javautils.sockets;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import eu.derzauberer.javautils.action.ClientConnectAction;
import eu.derzauberer.javautils.action.ClientDisconnectAction;
import eu.derzauberer.javautils.action.ClientMessageReceiveAction;
import eu.derzauberer.javautils.action.ClientMessageSendAction;
import eu.derzauberer.javautils.util.Sender;

public class Server implements Sender, Closeable {
	
	private ServerSocket server;
	private ExecutorService service;
	private int clientTimeout;
	private MessageType defaultMessageType;
	private ClientMessageReceiveAction messageReceiveAction;
	private ClientMessageSendAction messageSendAction;
	private ClientConnectAction connectAction;
	private ClientDisconnectAction disconnectAction;
	private ArrayList<Client> clients;
	
	public Server(int port) throws IOException {
		this(new ServerSocket(port));
	}
	
	public Server(ServerSocket server) throws IOException {
		this.server = server;
		clients = new ArrayList<>();
		service = Executors.newCachedThreadPool();
		final Thread thread = new Thread(this::inputLoop);
		clientTimeout = 0;
		defaultMessageType = MessageType.DEFAULT;
		thread.start();
	}
	
	private void inputLoop() {
		try {
			while (!server.isClosed()) {
				try {
					final Socket socket = server.accept();
					final Client client = new Client(socket, this);
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
	
	@Override
	public void sendInput(String input) {}
	
	@Override
	public void sendOutput(String message, MessageType type) {
		for (Client client : clients) {
			client.sendOutput(message, type);
		}
	}
	
	public void setMessageReceiveAction(ClientMessageReceiveAction messageReceiveAction) {
		this.messageReceiveAction = messageReceiveAction;
	}
	
	public ClientMessageReceiveAction getMessageReceiveAction() {
		return messageReceiveAction;
	}
	
	public void setMessageSendAction(ClientMessageSendAction messageSendAction) {
		this.messageSendAction = messageSendAction;
	}
	
	public ClientMessageSendAction getMessageSendAction() {
		return messageSendAction;
	}
	
	public void setConnectAction(ClientConnectAction connectAction) {
		this.connectAction = connectAction;
	}
	
	public ClientConnectAction getConnectAction() {
		return connectAction;
	}
	
	public void setDisconnectAction(ClientDisconnectAction disconnectAction) {
		this.disconnectAction = disconnectAction;
	}
	
	public ClientDisconnectAction getDisconnectAction() {
		return disconnectAction;
	}
	
	public void setServerTimeout(int timeout) {
		try {
			server.setSoTimeout(timeout);
		} catch (SocketException exception) {}
	}
	
	public int getServerTimeout() {
		try {
			return server.getSoTimeout();
		} catch (IOException exception) {
			return 0;
		}
	}
	
	public void setClientTimeout(int timeout) {
		clientTimeout = timeout;
		for (Client client : getClients()) {
			if (!client.isClosed()) client.setTimeout(timeout);
		}
	}
	
	public int getClientTimeout() {
		return clientTimeout;
	}
	
	public InetAddress getAdress() {
		return server.getInetAddress();
	}
	
	public int getLocalPort() {
		return server.getLocalPort();
	}
	
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
	
	public boolean isClosed() {
		return server.isClosed();
	}
	
	public ArrayList<Client> getClients() {
		return clients;
	}
	
	@Override
	public void setDefaultMessageType(MessageType type) {
		defaultMessageType = type;
		
	}

	@Override
	public MessageType getDefaultMessageType() {
		return defaultMessageType;
	}

}