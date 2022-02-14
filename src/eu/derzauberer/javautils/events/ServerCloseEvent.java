package eu.derzauberer.javautils.events;

import eu.derzauberer.javautils.util.Event;
import eu.derzauberer.javautils.util.Server;

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