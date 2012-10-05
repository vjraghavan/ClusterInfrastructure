package api;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.UUID;
import system.Computer;
import system.Shared;

/**
 * Represents any task which involves combinatorial optimizations that can
 * solved using Branch and Bound Paradigm
 * 
 * @author Vijayaraghavan Subbaiah
 */
public abstract class TaskImpl<T> implements Task<T> {
	/**
	 * Gets the Shared upper Bound value which is then used for bounding or
	 * branching a subtree
	 * 
	 * @return Returns the Shared upper bound instance which is broadcasted
	 *         across workers in the compute space for this task
	 * 
	 * @throws RemoteException
	 */
	public abstract Shared<?> getShared() throws RemoteException;

	/**
	 * Sets the Shared object to be used to broadcast messages across workers in
	 * the compute space for this task
	 * 
	 * @param shared
	 *            Shared object to be used to broadcast messages across workers
	 *            in the compute space for this task
	 * 
	 * @throws RemoteException
	 */
	protected abstract void setShared(Shared<?> shared) throws RemoteException;

	/**
	 * Validates whether the task has completed execution or it can be further
	 * decomposed
	 * 
	 * @return true if this task has completed its execution and false if it is
	 *         not
	 */
	public boolean isComplete() {
		return false;
	}

	/**
	 * Gets the tight lower bound of the task which is then used to make a
	 * decision whether to prune the subtree or further explore the subtree
	 * 
	 * @return Returns the tight lower bound used to prune the sub tree
	 */
	public <U> U getLowerBound() {
		return null;
	}

	/**
	 * Sets the instance of the Compute Engine in which the current task is
	 * executing
	 * 
	 * @param computer
	 *            Represents an instance of the Compute Engine
	 */
	public abstract void setComputer(Computer computer);

	/**
	 * Validates whether the task can be further decomposed or not
	 * 
	 * @return Returns whether it can be decomposed or not
	 */
	@Override
	public boolean isDecomposable() {
		return false;
	}

	/**
	 * Defines the execution carried out on a remote Compute Engine
	 * 
	 * @return Result of the execution of the task
	 * @throws RemoteException
	 */
	@Override
	public Result<T> execute() throws RemoteException {
		return null;
	}

	/**
	 * Defines how the given task is broken up into {@link api.Task subTasks}.
	 * 
	 * @param successorID
	 *            Represents the unique ID with which Successors responsible for
	 *            collecting the result of this {@link api.Task Task} is
	 *            identified.
	 * @return Return list of sub tasks generated
	 */

	@Override
	public LinkedList<Task<?>> generateTasks(UUID successorID, Computer computer)
			throws RemoteException {
		return null;
	}

	/**
	 * Defines how the Successor Task is generated for composing the results of
	 * the {@link api.Task subTasks}.
	 * 
	 * @return Represents the Successor Task.
	 */

	@Override
	public SuccessorTask<?> generateSuccessorTask(int jointCounter)
			throws RemoteException {
		return null;
	}

	/**
	 * Defines how the results are combined into partial Result which may be the
	 * final Solution or it can be composed further in the upper level into the
	 * solution for the overall problem
	 * 
	 * @return Returns the combined result
	 * @throws RemoteException
	 */

	@Override
	public Result<?> composeResult() throws RemoteException {
		return null;
	}

	/**
	 * Represents the current level of Decompose state in the divide and conquer
	 * paradigm
	 * 
	 * @return returns the level at which this task belongs to.
	 */

	@Override
	public int getLevelNumber() {
		return 0;
	}

	/**
	 * Gets the Two Types of tasks Space can execute. Child tasks will be the
	 * one created for dividing the problem into sub problems. Successor tasks
	 * are the ones creating for conquering the results returned and compose
	 * them into a solution for the overall problem
	 * 
	 * @return Returns the type of task
	 */

	@Override
	public TaskType getTaskType() {
		return null;
	}

	/**
	 * Gets an unique ID which identifies the Successor to which
	 * {@link api.Task Task} submitted for computation has to return its
	 * results.
	 * 
	 * @return Returns an unique ID which identifies the Successor for this
	 *         {@link api.Task Task}.
	 */

	@Override
	public UUID getSuccessorID() {
		return null;
	}

	/**
	 * Gets the instance of the Compute Engine in which the current task resides
	 * 
	 * @return Returns the instance of the Compute Engine in which the task
	 *         resides
	 */
	public abstract Computer getComputer();
}
