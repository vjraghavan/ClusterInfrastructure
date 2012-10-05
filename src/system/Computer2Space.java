package system;

import java.util.UUID;

import tasks.TaskContainer;
import api.Result;

/**
 * Defines an interface to register remote computers ({@link system.Computer
 * Computer}) with Compute {@link api.Space Space}.
 * 
 * @author Vijayaraghavan Subbaiah
 */

public interface Computer2Space extends java.rmi.Remote {

	public String getServiceName() throws java.rmi.RemoteException;

	/**
	 * Registers remote Computers with the compute Space
	 * 
	 * @param computer
	 *            Represents the {@system.Computer Computer} that is available
	 *            for registering itself with the {@link api.Space Space}.
	 * @throws java.rmi.RemoteException
	 *             Throws RemoteException if {@system.Computer Computer} is
	 *             unable to register itself with the {@link api.Space Space}
	 */

	String register(Computer computer) throws java.rmi.RemoteException;

	void setShared(Shared<?> shared) throws java.rmi.RemoteException;

	void putGeneratedTasks(TaskContainer tc, UUID taskID)
			throws java.rmi.RemoteException;

	void putResults(Result<?> result, UUID taskID)
			throws java.rmi.RemoteException;
}
