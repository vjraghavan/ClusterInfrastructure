package client;

import java.rmi.RemoteException;

import api.Space;

/**
 * Defines how the problem is broken up into tasks {@link api.Task Task} and
 * later, combines the solutions for subdivided tasks into solution for the
 * overall problem
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */

public interface Job {

	/**
	 * Defines how the problem is broken up into tasks {@link api.Task Task} by
	 * clients.
	 * 
	 * @param space
	 *            Represents the remote Space to which Tasks {@link api.Task
	 *            Task} are sent for execution.
	 */

	void generateTasks(Space space) throws RemoteException;

	/**
	 * Defines how the solutions to subdivided tasks are combined to solution to
	 * overall problem
	 * 
	 * @param space
	 *            Represents the remote Space to which Tasks {@link api.Task
	 *            Task} are sent for execution.
	 */

	void collectResults(Space space) throws RemoteException;

	/**
	 * Defines a method to get the complete solution to the problem specified by
	 * the clients.
	 * 
	 * @return Represents complete result for the problem specified by clients.
	 */

	Object getOverallResult();
}
