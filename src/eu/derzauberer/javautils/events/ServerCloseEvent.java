package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.sockets.Server;

public class ServerCloseEvent extends Event {

private Server server;
	
	public ServerCloseEvent(Server server) {
		this.server = server;
		execute();
	}
	
	public Server getServer() {
		return server;
	}
	
}