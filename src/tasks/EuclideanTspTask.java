package tasks;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
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

/**
 * Computes an optimal solution for the <a
 * href="http://en.wikipedia.org/wiki/Travelling_salesman_problem">Travelling
 * Salesman Problem</a>
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class EuclideanTspTask extends TaskImpl<TSPResult> implements
		Serializable {
	private static final long serialVersionUID = 229L;
	@SuppressWarnings("unused")
	private double[][] cities;
	int levelNumber;
	Task.TaskType type;
	private static final int MAX_LEVEL = 3;
	ArrayList<Integer> visitedCities;
	ArrayList<Integer> currentCities;
	double visitedDistance = 0;
	UUID successorID;
	UUID taskID;
	TspUpperBound tspUpperBound;
	Computer computer;
	double distance[][];
	double boundDistance[][];
	double lowerBound;

	/**
	 * @param levelNumber
	 *            Represents the level of the decompose phase of divide and
	 *            conquer paradigm
	 * @param cities
	 *            Represents the x and y coordinates of cities. cities[i][0] is
	 *            the x-coordinate of city[i] and cities[i][1] is the
	 *            y-coordinate of city[i].
	 * @param type
	 *            Represents {@link api.Type }
	 */

	public EuclideanTspTask(int levelNumber, double[][] cities, TaskType type) {
		this.cities = cities;
		this.type = type;
		successorID = UUID.randomUUID();
		taskID = UUID.randomUUID();
		currentCities = new ArrayList<Integer>();
		for (int i = 1; i < cities.length; i++) {
			currentCities.add(i);
		}
		visitedCities = new ArrayList<Integer>();
		visitedCities.add(0);
		int length = cities.length;
		distance = new double[length][length];
		boundDistance = new double[length][2];
		for (int i = 0; i < length; i++) {
			distance[i][i] = 0;
			for (int j = 0; j < length; j++) {
				if (i != j) {
					distance[i][j] = euclideanDistance(cities[i][0],
							cities[i][1], cities[j][0], cities[j][1]);
				}
			}
		}
		double min = Double.MAX_VALUE;
		double secondMin = Double.MAX_VALUE;
		for (int i = 0; i < length; i++) {
			min = Double.MAX_VALUE;
			secondMin = Double.MAX_VALUE;
			boolean first = true;
			for (int j = 0; j < length; j++) {
				if (i != j) {
					if (first) {
						min = distance[i][j];
						first = false;
					} else {
						if (distance[i][j] < min) {
							min = distance[i][j];
						} else if (distance[i][j] < secondMin) {
							secondMin = distance[i][j];
						}
					}
				}
			}
			boundDistance[i][0] = min;
			boundDistance[i][1] = secondMin;
		}

		for (int i = 0; i < length; i++) {
			System.out.println(boundDistance[i][0] + "   "
					+ boundDistance[i][1]);
		}
	}

	/**
	 * 
	 * @param levelNumber
	 *            Represents the level of decomposition phase in the divide and
	 *            conquer strategy
	 * @param cities
	 *            Represents the x and y coordinates of cities. cities[i][0] is
	 *            the x-coordinate of city[i] and cities[i][1] is the
	 *            y-coordinate of city[i].
	 * @param startCity
	 *            Denotes the city which is the start point of the tour
	 * @param currentCities
	 *            Denotes the rest of cities other than start city which will be
	 *            considered for making a tour from the start city
	 * @param successorID
	 *            Represents the unique ID of the successor to which this task
	 *            should return its result
	 * @param type
	 *            Represents the {@link api.Task.TaskType}
	 */
	@SuppressWarnings("unchecked")
	private EuclideanTspTask(int levelNumber, ArrayList<Integer> visitedCities,
			double visitedDistance, ArrayList<Integer> currentCities,
			UUID successorID, TaskType type, double[][] distance,
			double lowerBound, double[][] boundDistance) {
		this.levelNumber = levelNumber;
		this.visitedCities = (ArrayList<Integer>) visitedCities.clone();
		this.visitedDistance = visitedDistance;
		this.currentCities = (ArrayList<Integer>) currentCities.clone();
		this.successorID = successorID;
		this.type = type;
		this.distance = distance;
		this.lowerBound = lowerBound;
		this.boundDistance = boundDistance;
		taskID = UUID.randomUUID();
	}

	int[] arrayFromArrayList(ArrayList<Integer> arrList) {
		int[] arr = new int[arrList.size()];
		int i = 0;
		for (Integer element : arrList) {
			arr[i] = element;
			i++;
		}
		return arr;
	}

	/**
	 * 
	 * Generates the solution to the Travelling Salesman Problem.
	 * 
	 * @return An array containing cities in order that constitute an optimal
	 *         solution to TSP
	 * @throws RemoteException
	 * 
	 * @see api.Task Task
	 */

	@Override
	public Result<TSPResult> execute() throws RemoteException {
		Stack<EuclideanTspTask> stack = new Stack<EuclideanTspTask>();
		TSPResult result = null;
		Double minDistance = Double.MAX_VALUE;
		int[] minTour = null;
		EuclideanTspTask interSolution;
		EuclideanTspTask bestSolution = null;
		Computer comp = getComputer();
		TspUpperBound computerUpperBound = (TspUpperBound) getShared();
		TspUpperBound optimalUpperBound = new TspUpperBound(
				computerUpperBound.get());
		int levelNumber = this.levelNumber;
		UUID successorID = this.successorID;
		for (stack.push(this); !stack.isEmpty();) {
			interSolution = stack.pop();
			TspUpperBound currentLowerBound = new TspUpperBound(
					interSolution.getLowerBound());
			if (currentLowerBound.isNewerThan(optimalUpperBound)) {
				if (interSolution.isComplete()) {
					optimalUpperBound = new TspUpperBound(
							currentLowerBound.get());
					bestSolution = interSolution;
					minDistance = currentLowerBound.get();
					setShared(optimalUpperBound);
				} else {
					LinkedList<Task<?>> childTasks = interSolution
							.generateTasks(null, comp);
					Iterator<Task<?>> iterator = childTasks.iterator();
					while (iterator.hasNext()) {
						EuclideanTspTask childTask = ((EuclideanTspTask) iterator
								.next());
						stack.push(childTask);
					}
				}
			}
		}
		// if (optimalUpperBound.isNewerThan(computerUpperBound)) {
		if (minDistance != Double.MAX_VALUE) {

			// setShared(optimalUpperBound);
			int index = 0;
			minTour = new int[bestSolution.visitedCities.size()];
			Iterator<Integer> visitedIterator = bestSolution.visitedCities
					.iterator();
			while (visitedIterator.hasNext()) {
				minTour[index] = visitedIterator.next();
				index++;
			}
			result = new TSPResult(minTour, minDistance);
		} else
			result = null;
		return new ResultImpl<TSPResult>(levelNumber - 1, result, successorID);
	}

	/**
	 * Computes the <a
	 * href="http://en.wikipedia.org/wiki/Euclidean_distance">Euclidean
	 * Distance</a> between two cities
	 * 
	 * @param x1
	 *            Represents X-coordinate of the first city
	 * @param y1
	 *            Represents Y-coordinate of first city
	 * @param x2
	 *            Represents X-coordinate of the second city
	 * @param y2
	 *            Represents Y-coordinate of the second city
	 * 
	 * @return Returns Euclidean Distance between two cities
	 */

	private double euclideanDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
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
	 * Defines the implementation of the result composition of divide and
	 * conquer paradigm
	 * 
	 * @return Returns the computed result
	 */
	@Override
	public Result<?> composeResult() {
		return null;
	}

	/**
	 * Represents the implementation of how the successor task for EuclideanTSP
	 * problem is generated
	 * 
	 * @return Returns the generated successor task
	 */
	@Override
	public SuccessorTask<?> generateSuccessorTask(int jointCounter)
			throws RemoteException {
		return new EuclideanTspSuccessorTask<int[]>(new AtomicInteger(
				jointCounter), this.getLevelNumber(),
				Task.TaskType.SUCCESSORTASK, this.successorID);
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
	 * Represents the implementation of how the EuclideanTsp tasks are further
	 * decomposed
	 * 
	 * @return Returns the generated list of tasks
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LinkedList<Task<?>> generateTasks(UUID successorID, Computer computer)
			throws RemoteException {
		LinkedList<Task<?>> taskList = new LinkedList<Task<?>>();
		int newStartCity;
		double newVisitedDistance = 0;
		int previousVisitedCity = visitedCities.get(visitedCities.size() - 1);
		ArrayList<Integer> newCurrentCities;
		ArrayList<Integer> newVisitedCities;
		for (int i = 0; i < currentCities.size(); i++) {
			newCurrentCities = (ArrayList<Integer>) currentCities.clone();
			newStartCity = newCurrentCities.remove(i);
			newVisitedCities = (ArrayList<Integer>) visitedCities.clone();
			newVisitedCities.add(newStartCity);
			newVisitedDistance = visitedDistance
					+ distance[previousVisitedCity][newStartCity];
			int lastCity = newStartCity;
			double lowerBound = 0;
			if (newCurrentCities.size() == 0)
				lowerBound = newVisitedDistance + distance[lastCity][0];
			else {
				lowerBound = newVisitedDistance
						+ ((boundDistance[lastCity][1] + boundDistance[newCurrentCities
								.get(0)][0]) / 2);
			}
			Iterator<Integer> iter = newCurrentCities.iterator();
			if (iter.hasNext()) {
				int next = 0;
				int start = iter.next();
				while (iter.hasNext()) {
					next = iter.next();
					lowerBound += (boundDistance[start][1] + boundDistance[next][0]) / 2;
					start = next;
				}
				lowerBound += (boundDistance[start][1] + boundDistance[0][1]) / 2;
			}
			Double upperBound = ((TspUpperBound) computer.getShared()).get();
			if (lowerBound < upperBound) {
				EuclideanTspTask partialEuclideanTspTask = new EuclideanTspTask(
						this.getLevelNumber() + 1, newVisitedCities,
						newVisitedDistance, newCurrentCities, successorID,
						TaskType.CHILDTASK, distance, lowerBound, boundDistance);
				taskList.add(partialEuclideanTspTask);
			}
		}
		return taskList;
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
	 * Denotes the maximum level upto which tasks can be divided in divide and
	 * conquer paradigm
	 * 
	 * @return returns the maximum level limit
	 */
	public int getMaxLevel() {
		return MAX_LEVEL;
	}

	/**
	 * Validates whether the task can be further decomposed or not
	 * 
	 * @return Returns whether it can be decomposed or not
	 */
	@Override
	public boolean isDecomposable() {
		if (levelNumber < getMaxLevel())
			return true;
		else
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
	 * Validates whether the task has completed execution or it can be further
	 * decomposed
	 * 
	 * @return true if this task has completed its execution and false if it is
	 *         not
	 */

	public boolean isComplete() {
		if (visitedCities.size() == boundDistance.length)
			return true;
		else
			return false;
	}

	/**
	 * Gets the tight lower bound of the task which is then used to make a
	 * decision whether to prune the subtree or further explore the subtree
	 * 
	 * @return Returns the tight lower bound used to prune the sub tree
	 */

	@SuppressWarnings("unchecked")
	public Double getLowerBound() {
		return lowerBound;
	}

	/**
	 * Gets the instance of the Compute Engine in which the current task resides
	 * 
	 * @return Returns the instance of the Compute Engine in which the task
	 *         resides
	 */

	@Override
	public Computer getComputer() {
		return computer;
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
	public TaskContainer generate(UUID successorID, Computer computer)
			throws RemoteException {
		LinkedList<Task<?>> childTasksList = generateTasks(successorID,
				computer);
		SuccessorTask<?> successorTask = generateSuccessorTask(childTasksList
				.size());
		return new TaskContainer(successorTask, childTasksList);
	}

	@Override
	public UUID getTaskID() {
		return taskID;
	}
}
