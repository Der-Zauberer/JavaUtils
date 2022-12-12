package eu.derzauberer.javautils.util;

/**
 * Represents a functional interface to execute a command.
 */
public interface Command {
	
	/**
	 * Executes a command and uses the sender which called the command. The label is
	 * the first token of the command string, all other tokens are passed as
	 * arguments.
	 * 
	 * <pre>
	 * test arg1 args2 args3
	 * ^^^^ ^^^^^^^^^^^^^^^^
	 * label arguments
	 * </pre>
	 * 
	 * @param sender the source which called the command
	 * @param label the name of the command
	 * @param args the arguments of the command
	 * @return if the execution was successful
	 * @throws Exception any exception that occurs on execution
	 */
	boolean executeCommand(Sender sender, String label, String args[]) throws Exception;

}
