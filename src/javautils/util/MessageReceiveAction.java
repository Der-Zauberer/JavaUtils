package javautils.util;

public interface MessageReceiveAction {
	
	public abstract void onMessageReceive(Client client, String message);

}
