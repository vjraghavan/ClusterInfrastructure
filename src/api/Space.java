package api;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import system.Shared;

/**
 * Represents a raw computing resource where tasks ({@link api.Task Task}) are
 * automatically executed by registered workers as soon as they are dropped into
 * the space. It acts as a natural conduit for passing messages between master
 * and workers. Refer <a href="http://www.java.net/print/219571">How to build a
 * compute farm</a>.
 * 
 * @author Vijayaraghavan Subbaiah
 */

public interface Space extends java.rmi.Remote {

	/**
	 * Service exposed to the Clients and {@link system.Computer Computers}
	 */

	public static String SERVICE_NAME = "Space";

	/**
	 * Defines how the client decomposes the problem into a set of
	 * {@link api.Task Task} objects, and passes them to the {@link api.Space
	 * Space}
	 * 
	 * @param task
	 *            Represents {@link api.Task Task} to be executed.
	 * 
	 * @throws java.rmi.RemoteException
	 *             Throws RemoteException when there is a problem in writing the
	 *             tasks to the remote Compute Space
	 */

	public void put(Task<?> task) throws java.rmi.RemoteException;

	/**
	 * Defines how the client retrieves the associated {@link api.Result Result}
	 * objects via the take method. This method blocks until a result is
	 * available to return to the client.
	 * 
	 * @return Returns the associated {@link api.Result Result} objects for the
	 *         submitted {@link api.Task Task} Objects.
	 * @throws java.rmi.RemoteException
	 *             Throws RemoteException when there is a problem in reading the
	 *             results from the remote Compute Space
	 */

	Result<?> take() throws java.rmi.RemoteException;

	/**
	 * Defines a deployment convenience to stop each registered
	 * {@link system.Computer Computer} and then stops itself.
	 * 
	 * @throws java.rmi.RemoteException
	 *             Throws RemoteException when registered computers are stopped.
	 */

	void shutdown() throws java.rmi.RemoteException;

	/**
	 * The client passes a {@link api.Task Task} object representing a complex
	 * computation to the Space via this method. In principle, these
	 * {@link api.Task Task} objects are processed in parallel by workers by the
	 * Space. This method blocks until a {@link api.Result Result} object
	 * containing the result of the task is available to be returned to the
	 * client.
	 * 
	 * @param task
	 *            Task to be added to the Compute Space
	 * @param shared
	 *            Shared object to be used to broadcast messages across workers
	 *            in the compute space for this task
	 * 
	 * @throws java.rmi.RemoteException
	 *             Thrown if any read/write errors occur during the process of
	 *             adding a task to the queue in the compute space or if any
	 *             errors occur during resource allocation in the compute space
	 * 
	 * @return One of the results obtained upon completion of individual tasks
	 */
	Result<?> compute(Task<?> task, Shared<?> shared) throws RemoteException;

	/**
	 * Sets the local registry where the space is instantiated
	 * 
	 * @param registry
	 *            Local registry service where the Space is registered
	 * @throws RemoteException
	 */
	public void setRegistry(Registry registry) throws RemoteException;
}
