package eu.derzauberer.javautils.action;

import java.io.File;

public interface FileUpdatedAction {

	public abstract void onAction(File file);
	
}
