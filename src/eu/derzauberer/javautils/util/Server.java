package eu.derzauberer.javautils.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import eu.derzauberer.javautils.action.ClientMessageReceiveAction;
import eu.derzauberer.javautils.events.ClientMessageReceiveEvent;
import eu.derzauberer.javautils.events.ServerCloseEvent;

public class Server implements Runnable {
	
	private ServerSocket server;
	private ArrayList<Client> clients;
	private ExecutorService service;
	private Thread thread;
	private ClientMessageReceiveAction action;
	private int clientTimeout;
	
	public Server(int port) throws IOException {
		this(new ServerSocket(port));
	}
	
	public Server(ServerSocket server) throws IOException {
		this.server = server;
		clients = new ArrayList<>();
		action = (event) -> {};
		service = Executors.newCachedThreadPool();
		thread = new Thread(this);
		clientTimeout = 0;
		thread.start();
	}
	
	@Override
	public void run() {
		try {
			while (!server.isClosed()) {
				try {
					Socket socket = server.accept();
					Client client = new Client(socket, this);
					client.setTimeout(clientTimeout);
					service.submit(client);
					clients.add(client);
				} catch (RejectedExecutionException exception) {}
			}
		} catch (SocketTimeoutException | SocketException exception) {
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		if (!server.isClosed()) close();
	}
	
	public void sendBroadcastMessage(String message) {
		for (Client client : clients) {
			client.sendMessage(message);
		}
	}
	
	protected void onMessageRecieve(ClientMessageReceiveEvent event) {
		action.onAction(event);
	}
	
	public void setOnMessageRecieve(ClientMessageReceiveAction action) {
		this.action = action;
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
			if (!client.isClosed()) {
				client.setTimeout(timeout);
			}
		}
	}
	
	public int getClientTimeout() {
		return clientTimeout;
	}
	
	public void close() {
		if (!server.isClosed()) {
			try {
				server.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			for (int i = 0; i < clients.size(); i++) {
				clients.get(i).close();
			}
			new ServerCloseEvent(this);
			service.shutdown();
		}
	}
	
	public boolean isClosed() {
		return server.isClosed();
	}
	
	public ServerSocket getServerSocket() {
		return server;
	}
	
	public ArrayList<Client> getClients() {
		return clients;
	}

}