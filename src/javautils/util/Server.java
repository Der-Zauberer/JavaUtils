package javautils.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
	
	private ServerSocket server;
	private ArrayList<Client> clients;
	private Thread thread;
	private MessageReceiveAction messagereceiveaction;
	
	public Server(int port) throws IOException {
		this(new ServerSocket(port));
	}
	
	public Server(ServerSocket server) throws IOException {
		this.server = server;
		clients = new ArrayList<>();
		messagereceiveaction = (client, message) -> {};
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		while (!thread.isInterrupted()) {
			try {
				Socket socket = this.server.accept();
				Client client = new Client(socket) {
					@Override
					public void onMessageReceive(String message) {
						onMessageRecieve(this, message);
					}
				};
				clients.add(client);
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
	
	public void onMessageRecieve(Client client, String message) {
		messagereceiveaction.onMessageReceive(client, message);
	}
	
	public void setOnMessageRecieve(MessageReceiveAction messagereceiveaction) {
		this.messagereceiveaction = messagereceiveaction;
	}
	
	public ServerSocket getServerSocket() {
		return server;
	}
	
	public ArrayList<Client> getClients() {
		return clients;
	}

}
