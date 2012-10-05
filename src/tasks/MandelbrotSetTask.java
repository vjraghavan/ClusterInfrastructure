package tasks;

import api.Result;
import api.SuccessorTask;
import api.Task;
import api.TaskImpl;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import system.Computer;
import system.ResultImpl;
import system.Shared;

/**
 * Computes the <a href="http://en.wikipedia.org/wiki/Mandelbrot_set">Mandelbrot
 * set</a> Solution
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class MandelbrotSetTask extends TaskImpl<Map<Integer, int[][]>>
		implements Serializable {
	private static final long serialVersionUID = 227L;
	private static final int MANDELBROTSET_LIMIT = 2;
	private static final int MAX_LEVEL = 5;
	int levelNumber;
	double lowerX;
	double lowerY;
	double edgeLength;
	int numRowSquares;
	int numColumnSquares;
	int iterationLimit;
	UUID successorID;
	UUID taskID;
	Task.TaskType type;
	int rowID;

	/**
	 * @param lowerX
	 *            X-coordinate of the lower left corner of a square in the
	 *            complex plane
	 * @param lowerY
	 *            Y-coordinate of the lower left corner of a square in the
	 *            complex plane
	 * @param edgeLength
	 *            Edge length of the square in the complex plane, whose sides
	 *            are parallel to the axes
	 * @param numRowSquares
	 *            Represents number of squares in each row of a Complex plane.
	 *            Complex plane subdivided into numRowSquares X numColumnSquares
	 *            squares, each of which is visualized by 1 pixel
	 * @param numColumnSquares
	 *            Represents number of squares in each column of a Complex
	 *            plane. Complex plane subdivided into numRowSquares X
	 *            numColumnSquares squares, each of which is visualized by 1
	 *            pixel
	 * @param iterationLimit
	 *            Defines upper bound number of iterations to decide, whether
	 *            the representative point of a region is in the Mandelbrot set.
	 * @param taskIdentifier
	 *            Represents the unique ID to identify the subdivided
	 *            MandelbrotSet tasks
	 */

	public MandelbrotSetTask(double lowerX, double lowerY, double edgeLength,
			int numRowSquares, int numColumnSquares, int iterationLimit,
			int levelNumber, int rowID, Task.TaskType type, UUID successorID) {
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.edgeLength = edgeLength;
		this.numRowSquares = numRowSquares;
		this.numColumnSquares = numColumnSquares;
		this.iterationLimit = iterationLimit;
		this.levelNumber = levelNumber;
		this.type = type;
		this.successorID = successorID;
		this.rowID = rowID;
		taskID = UUID.randomUUID();
	}

	/**
	 * Generates the solution for MandelbrotSet
	 * 
	 * @return An array containing results of whether each point in the complex
	 *         plane representing a pixel belongs to MandelbrotSet or not
	 * 
	 * @see api.Task Task
	 */

	public Result<Map<Integer, int[][]>> execute() {
		int[][] counts = new int[numRowSquares][numColumnSquares];
		int i = 0, j = 0;
		HashMap<Integer, int[][]> resultMap = new HashMap<Integer, int[][]>();
		for (double realIndex = lowerX; i < numRowSquares; realIndex += (edgeLength), i++) {
			j = numColumnSquares - 1;
			for (double imaginaryIndex = lowerY; j >= 0; imaginaryIndex += (edgeLength), j--) {
				double realConstant = realIndex;
				double imaginaryConstant = imaginaryIndex;
				double real = realIndex;
				double imaginary = imaginaryIndex;
				int iteration = 0;
				while (isMandelbrotSetBound(real, imaginary)
						&& (iteration < iterationLimit)) {
					double tempReal = real * real - imaginary * imaginary
							+ realConstant;
					imaginary = 2 * real * imaginary + imaginaryConstant;
					real = tempReal;
					iteration = iteration + 1;
				}
				counts[i][j] = iteration;
			}
		}
		resultMap.put(rowID, counts);
		Result<Map<Integer, int[][]>> result = new ResultImpl<Map<Integer, int[][]>>(
				this.getLevelNumber() - 1, resultMap, this.getSuccessorID());
		return result;
	}

	/**
	 * Determines whether the given point in the complex plane is a
	 * representative of MandelbrotSet
	 * 
	 * @param real
	 *            Represents real-coordinate of a point in the complex plane
	 * @param imaginary
	 *            Represents imaginary-coordinate of a point in the complex
	 *            plane
	 * 
	 * @return Returns whether it is MandelbrotSetBound or not
	 */

	private boolean isMandelbrotSetBound(double real, double imaginary) {
		return (Math.sqrt(real * real + imaginary * imaginary) < MANDELBROTSET_LIMIT) ? true
				: false;
	}

	/**
	 * Gets the ID of the MandelbrotSet task which uniquely identifies a
	 * particular subdivided task
	 * 
	 * @return Returns the unique ID of the subdivided MandelbrotSet task
	 */

	@Override
	public LinkedList<Task<?>> generateTasks(UUID successorID, Computer computer)
			throws RemoteException {
		LinkedList<Task<?>> listTasks = new LinkedList<Task<?>>();
		int i = 0;
		int newNumRowSquares = numRowSquares / 2;
		int newRowID;
		for (double realIndex = lowerX; i < 2; realIndex += (edgeLength * (newNumRowSquares)), i++) {
			newRowID = rowID + i * newNumRowSquares;
			MandelbrotSetTask partialMandelbrotSetTask = new MandelbrotSetTask(
					realIndex, lowerY, edgeLength, newNumRowSquares,
					numColumnSquares, iterationLimit, levelNumber + 1,
					newRowID, Task.TaskType.CHILDTASK, successorID);
			listTasks.add(partialMandelbrotSetTask);
		}
		return listTasks;
	}

	@Override
	public int getLevelNumber() {
		return levelNumber;
	}

	@Override
	public Result<?> composeResult() {
		return null;
	}

	@Override
	public SuccessorTask<?> generateSuccessorTask(int jointCounter)
			throws RemoteException {
		return new MandelbrotSetSuccessorTask<int[][]>(new AtomicInteger(
				jointCounter), this.getLevelNumber(),
				Task.TaskType.SUCCESSORTASK, this.successorID);
	}

	@Override
	public api.Task.TaskType getTaskType() {
		return type;
	}

	@Override
	public UUID getSuccessorID() {
		return successorID;
	}

	public int getMaxLevel() {
		return MAX_LEVEL;
	}

	@Override
	public boolean isDecomposable() {
		if (levelNumber < this.getMaxLevel())
			return true;
		else
			return false;
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
