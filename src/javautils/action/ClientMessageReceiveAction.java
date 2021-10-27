package javautils.action;

import javautils.util.Client;

public interface ClientMessageReceiveAction {
	
	public abstract void onAction(Client client, String message);

}
