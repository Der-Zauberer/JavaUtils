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

public class Server implements Closeable {
	
	private ServerSocket server;
	private ExecutorService service;
	private int clientTimeout;
	private ClientMessageReceiveAction clientMessageReceiveAction;
	private ClientMessageSendAction clientMessageSendAction;
	private ClientConnectAction clientConnectAction;
	private ClientDisconnectAction clientDisconnectAction;
	private ArrayList<Client> clients;
	
	public Server(int port) throws IOException {
		this(new ServerSocket(port));
	}
	
	public Server(ServerSocket server) throws IOException {
		this.server = server;
		clients = new ArrayList<>();
		service = Executors.newCachedThreadPool();
		Thread thread = new Thread(this::inputLoop);
		clientTimeout = 0;
		thread.start();
	}
	
	private void inputLoop() {
		try {
			while (!server.isClosed()) {
				try {
					Socket socket = server.accept();
					Client client = new Client(socket, this);
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
	
	public void sendBroadcastMessage(String message) {
		for (Client client : clients) {
			client.sendMessage(message);
		}
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

}