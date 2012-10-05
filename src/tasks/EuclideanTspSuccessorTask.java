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

public class EuclideanTspSuccessorTask<T> extends TaskImpl<T> implements
		SuccessorTask<T>, Serializable {
	private static final long serialVersionUID = -664116067616358753L;
	AtomicInteger jointCounter;
	LinkedList<Result<?>> resultList;
	int levelNumber;
	Task.TaskType type;
	UUID successorID;
	UUID taskID;
	double minDistance;
	Computer computer;

	/**
	 * 
	 * @param startCity
	 *            Denotes the city which is the start point of the tour
	 * @param cities
	 *            Represents the x and y coordinates of cities. cities[i][0] is
	 *            the x-coordinate of city[i] and cities[i][1] is the
	 *            y-coordinate of city[i].
	 * @param jointCounter
	 *            Represents the number of results this successor task waits for
	 *            before progressing
	 * @param levelNumber
	 *            represents the level of the decompose phase of divide and
	 *            conquer paradigm
	 * @param type
	 *            represents the task type
	 * @param successorID
	 *            represents the unique successor ID of this task
	 */
	EuclideanTspSuccessorTask(AtomicInteger jointCounter, int levelNumber,
			Task.TaskType type, UUID successorID) {
		this.jointCounter = jointCounter;
		this.levelNumber = levelNumber;
		this.type = type;
		resultList = new LinkedList<Result<?>>();
		this.successorID = successorID;
		minDistance = Double.MAX_VALUE;
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
	 * 
	 * Generates the solution to the Travelling Salesman Problem.
	 * 
	 * @return An array containing cities in order that constitute an optimal
	 *         solution to TSP
	 * 
	 * @see api.Task Task
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
	 * @throws RemoteException
	 */

	@Override
	public Result<?> composeResult() throws RemoteException {
		double minDistance = Double.MAX_VALUE;
		int[] minTour = null;
		TSPResult tspResult = null;
		Iterator<Result<?>> iterator = resultList.iterator();
		while (iterator.hasNext()) {
			Result<?> partialResult = iterator.next();
			if (partialResult != null) {
				tspResult = (TSPResult) partialResult.getTaskReturnValue();
				if (tspResult != null) {
					if (minDistance > tspResult.getMinDistance()) {
						minDistance = tspResult.getMinDistance();
						minTour = tspResult.getMinTour();
					}
				}
			}
		}
		if (minDistance == Double.MAX_VALUE)
			tspResult = null;
		else {
			/*
			 * if (minDistance <= ((TspUpperBound) getShared()).get()
			 * .doubleValue()) { tspResult = new TSPResult(minTour,
			 * minDistance); } else tspResult = null;
			 */
			tspResult = new TSPResult(minTour, minDistance);
		}
		return new ResultImpl<TSPResult>(levelNumber - 1, tspResult,
				this.successorID);
	}

	/**
	 * Represents the implementation of how the successor task for Euclideantsp
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
	 * Represents the implementation of how the Euclidean tasks are further
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

	/**
	 * Gets the Shared upper Bound value which is then used for bounding or
	 * branching a subtree
	 * 
	 * @return Returns the Shared upper bound instance which is broadcasted
	 *         across workers in the compute space for this task
	 * 
	 * @throws RemoteException
	 */
	@Override
	public Shared<?> getShared() throws RemoteException {
		return computer.getShared();
	}

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

	@Override
	protected void setShared(Shared<?> shared) throws RemoteException {
		computer.setShared(shared, false);
	}

	/**
	 * Sets the instance of the Compute Engine in which the current task is
	 * executing
	 * 
	 * @param computer
	 *            Represents an instance of the Compute Engine
	 */
	@Override
	public void setComputer(Computer computer) {
		this.computer = computer;
	}

	/**
	 * Gets the instance of the Compute Engine in which the current task resides
	 * 
	 * @return Returns the instance of the Compute Engine in which the task
	 *         resides
	 */
	@Override
	public Computer getComputer() {
		return null;
	}

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
	@Override
	public TaskContainer generate(UUID successorID, Computer computer) {
		return null;
	}

	@Override
	public UUID getTaskID() {
		return taskID;
	}
}
