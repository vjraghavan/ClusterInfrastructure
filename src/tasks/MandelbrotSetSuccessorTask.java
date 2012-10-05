package tasks;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import system.Computer;
import system.ResultImpl;
import system.Shared;
import api.Result;
import api.SuccessorTask;
import api.Task;
import api.TaskImpl;

public class MandelbrotSetSuccessorTask<T> extends TaskImpl<T> implements
		SuccessorTask<T>, Serializable {

	private static final long serialVersionUID = 7520590055646721525L;
	int[][] counts;
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
	MandelbrotSetSuccessorTask(AtomicInteger jointCounter, int levelNumber,
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
	 * Executes the computation for the calculation of MandelbrotSet
	 * 
	 * @return Represents the Mandelbrot computation Result array
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
		HashMap<Integer, int[][]> result = new HashMap<Integer, int[][]>();
		Iterator<Result<?>> iterator = resultList.iterator();
		while (iterator.hasNext()) {
			Result<?> partialResult = iterator.next();
			@SuppressWarnings("unchecked")
			Map<Integer, int[][]> partialSolution = (Map<Integer, int[][]>) partialResult
					.getTaskReturnValue();
			result.putAll(partialSolution);
		}
		if (levelNumber == 0) {
			int[][] resultCount = new int[1024][1024];
			int rowID;
			int[][] values;
			for (Map.Entry<Integer, int[][]> entry : result.entrySet()) {
				rowID = entry.getKey();
				values = entry.getValue();
				for (int i = 0; i < values.length; i++) {
					for (int j = 0; j < values[0].length; j++) {
						resultCount[rowID + i][j] = values[i][j];
					}
				}
			}
			result = new HashMap<Integer, int[][]>();
			result.put(0, resultCount);
		}
		return new ResultImpl<Map<Integer, int[][]>>(levelNumber - 1, result,
				this.successorID);
	}

	/**
	 * Represents the implementation of how the successor task for MandelbrotSet
	 * problem is generated
	 * 
	 * @return Returns the generated successor task
	 */

	@Override
	public SuccessorTask<?> generateSuccessorTask(int jointCounter)
			throws RemoteException {
		return null;
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
	 * Denotes the maximum level upto which tasks can be divided in divide and
	 * conquer paradigm
	 * 
	 * @return returns the maximum level limit
	 */

	public int getMaxLevel() {
		return 0;
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
