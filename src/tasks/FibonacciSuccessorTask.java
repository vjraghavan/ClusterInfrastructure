package tasks;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import system.Computer;
import system.ResultImpl;
import system.Shared;

import api.Result;
import api.SuccessorTask;
import api.Task;
import api.TaskImpl;

public class FibonacciSuccessorTask<T> extends TaskImpl<T> implements
		SuccessorTask<T>, Serializable {
	private static final long serialVersionUID = 3295373718507598079L;
	AtomicInteger jointCounter;
	LinkedList<Result<?>> resultList;
	int levelNumber;
	Task.TaskType type;
	UUID successorID;
	UUID taskID;

	/**
	 * 
	 * @param jointCounter
	 *            Represents the number of results successor is waiting for
	 * @param levelNumber
	 *            Represents the current level of the decompose phase of divide
	 *            and conquer paradigm
	 * @param type
	 *            Represents the task type
	 * @param successorID
	 *            Represents the unique id of the successor task
	 */
	FibonacciSuccessorTask(AtomicInteger jointCounter, int levelNumber,
			Task.TaskType type, UUID successorID) {
		this.jointCounter = jointCounter;
		this.levelNumber = levelNumber;
		this.type = type;
		resultList = new LinkedList<Result<?>>();
		this.successorID = successorID;
		taskID = UUID.randomUUID();
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
	 * Executes the computation for the calculation of nth fibonacci number
	 * 
	 * @return Represents the nth fibonacci number
	 */

	@Override
	public Result<T> execute() {
		return null;
	}

	/**
	 * Defines the implementation of the result composition of divide and
	 * conquer paradigm
	 * 
	 * @return Returns the computed result
	 */

	@Override
	public Result<?> composeResult() {
		int sum = 0;
		Iterator<Result<?>> iterator = resultList.iterator();
		while (iterator.hasNext()) {
			Result<?> partialResult = iterator.next();
			Integer partialSolution = (Integer) partialResult
					.getTaskReturnValue();
			sum += partialSolution;
		}
		return new ResultImpl<Integer>(levelNumber - 1, sum, this.successorID);
	}

	/**
	 * Represents the type of the task
	 * 
	 * @return Returns the actual type of task
	 */

	@Override
	public api.Task.TaskType getTaskType() {
		return this.type;
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
	 * Gateway for the tasks to submit the results to the successor which is
	 * waiting for the results before it can make progress
	 * 
	 * @param args
	 *            Represents the {@link api.Result} object.
	 */

	@Override
	public void putArguments(Result<?> args) {
		synchronized (resultList) {
			resultList.add(args);
			jointCounter.decrementAndGet();
		}
	}

	/**
	 * Represents number of results this successor is waiting for before it can
	 * make progress.
	 * 
	 * @return Returns the number of results needed for the successor to start
	 *         execution.
	 */

	@Override
	public int getJointCounter() {
		return jointCounter.get();
	}

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
	 * Represents the implementation of how the Fibonacci tasks are further
	 * decomposed
	 * 
	 * @return Returns the generated list of tasks
	 */
	@Override
	public LinkedList<Task<?>> generateTasks(UUID successorID, Computer computer)
			throws RemoteException {
		return null;
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
		return null;
	}

	@Override
	public TaskContainer generate(UUID successorID, Computer computer)
			throws RemoteException {
		return null;
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
