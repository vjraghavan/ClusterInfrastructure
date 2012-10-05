package system;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.UUID;

import tasks.TaskContainer;

import api.Result;
import api.Task;

/**
 * Enables execution of the tasks ({@link api.Task Task}) submitted to the
 * remote Compute {@link api.Space Space}.
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */

public interface Computer extends java.rmi.Remote {
	/**
	 * Defines the interface to execute tasks by the registered Computer.
	 * 
	 * @param task
	 *            Represents the {@link api.Task task} to be executed by
	 *            registered Computer.
	 * @return {@link api.Result Result} of the execution of the task
	 */

	Result<?> execute(Task<?> task) throws java.rmi.RemoteException;

	/**
	 * Defines a deployment convenience to stop registered Computer.
	 * 
	 * @throws java.rmi.RemoteException
	 *             Throws RemoteException when registered computer is stopped.
	 */
	void shutdown() throws java.rmi.RemoteException;

	/**
	 * Defines how the given task is broken up into {@link api.Task subTasks}.
	 * 
	 * @param successorId
	 * 
	 * @param task
	 *            Represents the {@link api.Task task} to be executed by
	 *            registered Computer.
	 * @return Returns list of the {@link api.Task subtasks} generated.
	 * */

	public LinkedList<Task<?>> generateTasks(Task<?> t, UUID successorId)
			throws RemoteException;

	public Result<?> composeResult(Task<?> t) throws RemoteException;

	public Task<?> generateSuccessorTask(Task<?> partialTask, int jointCounter)
			throws RemoteException;

	public void setShared(Shared<?> proposedShared, boolean bySpace)
			throws RemoteException;

	public Shared<?> getShared() throws RemoteException;

	public TaskContainer generate(Task<?> partialTask, UUID successorID)
			throws RemoteException;

	void putTasksInComputer(Task<?> task) throws RemoteException;

}
