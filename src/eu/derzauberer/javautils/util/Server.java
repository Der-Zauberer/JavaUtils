package eu.derzauberer.javautils.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import eu.derzauberer.javautils.action.ClientMessageReceiveAction;
import eu.derzauberer.javautils.events.ClientMessageRecieveEvent;

public class Server implements Runnable {
	
	private ServerSocket server;
	private ArrayList<Client> clients;
	private ExecutorService service;
	private Thread thread;
	private ClientMessageReceiveAction action;
	
	public Server(int port) throws IOException {
		this(new ServerSocket(port));
	}
	
	public Server(ServerSocket server) throws IOException {
		this.server = server;
		clients = new ArrayList<>();
		action = (event) -> {};
		service = Executors.newCachedThreadPool();
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		while (!thread.isInterrupted() && !server.isClosed()) {
			try {
				Socket socket = server.accept();
				Client client = new Client(socket, this);
				service.submit(client);
				clients.add(client);
			} catch (SocketException exception) {
				if (!server.isClosed()) close();
				return;
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
	
	protected void onMessageRecieve(ClientMessageRecieveEvent event) {
		action.onAction(event);
	}
	
	public void setOnMessageRecieve(ClientMessageReceiveAction action) {
		this.action = action;
	}
	
	public void close() {
		try {
			server.close();
			clients.forEach(client -> client.close());
			thread.interrupt();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	public boolean isClosed() {
		return server.isClosed();
	}

	public void reopen(int port) throws UnknownHostException, IOException {
		server = new ServerSocket(port);
		thread.start();
	}
	
	public ServerSocket getServerSocket() {
		return server;
	}
	
	public ArrayList<Client> getClients() {
		return clients;
	}
	
	public void removeClientFromHandler(Client client) {
		clients.remove(client);
	}

}