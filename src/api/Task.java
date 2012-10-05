package api;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.UUID;

import system.Computer;
import tasks.TaskContainer;

/**
 * Models any task that can be executed on a remote Compute Engine.
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */

public interface Task<T> {

	/**
	 * Represents the Two Types of tasks Space can execute. Child tasks will be
	 * the one created for dividing the problem into sub problems. Successor
	 * tasks are the ones creating for conquering the results returned and
	 * compose them into a solution for the overall problem
	 */
	enum TaskType {
		CHILDTASK, SUCCESSORTASK
	};

	/**
	 * Validates whether the task can be further decomposed or not
	 * 
	 * @return Returns whether it can be decomposed or not
	 */
	public boolean isDecomposable();

	/**
	 * Defines the execution carried out on a remote Compute Engine
	 * 
	 * @return Result of the execution of the task
	 * @throws RemoteException
	 */

	Result<T> execute() throws RemoteException;

	/**
	 * Defines how the given task is broken up into {@link api.Task subTasks}.
	 * 
	 * @param successorID
	 *            Represents the unique ID with which Successors responsible for
	 *            collecting the result of this {@link api.Task Task} is
	 *            identified.
	 * @return Return list of sub tasks generated
	 */

	public LinkedList<Task<?>> generateTasks(UUID successorID, Computer computer)
			throws RemoteException;

	/**
	 * Defines how the Successor Task is generated for composing the results of
	 * the {@link api.Task subTasks}.
	 * 
	 * @return Represents the Successor Task.
	 */

	public SuccessorTask<?> generateSuccessorTask(int jointCounter)
			throws RemoteException;

	/**
	 * Defines how the results are combined into partial Result which may be the
	 * final Solution or it can be composed further in the upper level into the
	 * solution for the overall problem
	 * 
	 * @return Returns the combined result
	 * @throws RemoteException
	 */
	public Result<?> composeResult() throws RemoteException;

	/**
	 * Represents the current level of Decompose state in the divide and conquer
	 * paradigm
	 * 
	 * @return returns the level at which this task belongs to.
	 */
	public int getLevelNumber();

	/**
	 * Gets the Two Types of tasks Space can execute. Child tasks will be the
	 * one created for dividing the problem into sub problems. Successor tasks
	 * are the ones creating for conquering the results returned and compose
	 * them into a solution for the overall problem
	 * 
	 * @return Returns the type of task
	 */

	public Task.TaskType getTaskType();

	/**
	 * Gets an unique ID which identifies the Successor to which
	 * {@link api.Task Task} submitted for computation has to return its
	 * results.
	 * 
	 * @return Returns an unique ID which identifies the Successor for this
	 *         {@link api.Task Task}.
	 */
	public UUID getSuccessorID();

	/**
	 * Generates the list of subtasks and also its successor for collecting the
	 * results
	 * 
	 * @param successorID
	 *            Represents the unique ID with which Successors responsible for
	 *            collecting the result of this {@link api.Task Task} is
	 *            identified.
	 * @param computer
	 *            Represents the Compute Engine for executing the tasks
	 *            submitted by the clients
	 * @return A complex Object containing both the list of subtasks and also
	 *         its associated successor
	 * @throws RemoteException
	 */
	public TaskContainer generate(UUID successorID, Computer computer)
			throws RemoteException;

	/**
	 * Represents the unique task identifier with which tasks are identified.
	 * 
	 * @return Returns the unique task identifier with which tasks are
	 *         identified.
	 */
	public UUID getTaskID();
}
