package tasks;

import java.util.UUID;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import system.Computer;
import system.ResultImpl;
import system.Shared;
import api.Result;
import api.SuccessorTask;
import api.Task;
import api.TaskImpl;

/**
 * Computes the Nth fibonacci number
 * 
 * @author Vijayaraghavan Subbaiah
 */
public class FibonacciTask extends TaskImpl<Integer> implements Serializable {
	int levelNumber;
	Task.TaskType type;
	UUID successorID;
	UUID taskID;
	private static final long serialVersionUID = 8682154316880350190L;
	int n;

	/**
	 * 
	 * @param levelNumber
	 *            Represents the current level of the decompose phase of divide
	 *            and conquer paradigm
	 * @param n
	 *            Represents the nth fibonacci number to be computed
	 * @param type
	 *            Represents the task type
	 * @param successorID
	 *            Represents the unique id of the successor task
	 */
	public FibonacciTask(int levelNumber, int n, TaskType type, UUID successorID) {
		this.n = n;
		this.levelNumber = levelNumber;
		this.type = type;
		this.successorID = successorID;
		taskID = UUID.randomUUID();
	}

	/**
	 * Executes the computation for the calculation of nth fibonacci number
	 * 
	 * @return Represents the nth fibonacci number
	 */
	@Override
	public Result<Integer> execute() {
		return new ResultImpl<Integer>(this.getLevelNumber() - 1, n,
				this.getSuccessorID());
	}

	/**
	 * Represents the implementation of how the Fibonacci tasks are further
	 * decomposed
	 * 
	 * @return Returns the generated list of tasks
	 */
	@Override
	public LinkedList<Task<?>> generateTasks(UUID successorID, Computer computer)
			throws RemoteException {
		LinkedList<Task<?>> listTasks = new LinkedList<Task<?>>();
		FibonacciTask partialFibonacciTask1 = new FibonacciTask(
				levelNumber + 1, n - 1, Task.TaskType.CHILDTASK, successorID);
		listTasks.add(partialFibonacciTask1);
		FibonacciTask partialFibonacciTask2 = new FibonacciTask(
				levelNumber + 1, n - 2, Task.TaskType.CHILDTASK, successorID);
		listTasks.add(partialFibonacciTask2);
		return listTasks;
	}

	/**
	 * Represents the implementation of how the successor task for Fibonacci
	 * problem is generated
	 * 
	 * @return Returns the generated successor task
	 */
	@Override
	public SuccessorTask<?> generateSuccessorTask(int jointCounter)
			throws RemoteException {
		return new FibonacciSuccessorTask<Integer>(new AtomicInteger(
				jointCounter), this.getLevelNumber(),
				Task.TaskType.SUCCESSORTASK, this.successorID);
	}

	/**
	 * Defines the implementation of the result composition of divide and
	 * conquer paradigm
	 * 
	 * @return Returns the computed result
	 */

	@Override
	public Result<?> composeResult() {
		System.out.println("calling compfibTask");
		return null;
	}

	/**
	 * Represents the current level of the decompose phase in which the task is
	 * in
	 * 
	 * @return Returns the current level of the decompose phase in which the
	 *         task is in
	 */
	@Override
	public int getLevelNumber() {
		return levelNumber;
	}

	/**
	 * Represents the type of the task
	 * 
	 * @return Returns the actual type of task
	 */
	@Override
	public api.Task.TaskType getTaskType() {
		return type;
	}

	/**
	 * Gets the unique successor ID for this task
	 * 
	 * @return Represents the unique id of the successor
	 */
	@Override
	public UUID getSuccessorID() {
		return successorID;
	}

	/**
	 * Validates whether the task can be further decomposed or not
	 * 
	 * @return Returns whether it can be decomposed or not
	 */
	@Override
	public boolean isDecomposable() {
		if (n == 0 || n == 1)
			return false;
		else
			return true;
	}

	@Override
	public TaskContainer generate(UUID successorID, Computer computer)
			throws RemoteException {
		LinkedList<Task<?>> childTasksList = generateTasks(successorID,
				computer);
		SuccessorTask<?> successorTask = generateSuccessorTask(2);
		return new TaskContainer(successorTask, childTasksList);
	}

	@Override
	public Shared<?> getShared() throws RemoteException {
		return null;
	}

	@Override
	protected void setShared(Shared<?> shared) throws RemoteException {
	}

	@Override
	public void setComputer(Computer computer) {
	}

	@Override
	public Computer getComputer() {
		return null;
	}

	@Override
	public UUID getTaskID() {
		return taskID;
	}
}