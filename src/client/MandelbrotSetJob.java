package client;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.UUID;

import tasks.MandelbrotSetTask;

import api.Result;
import api.Space;
import api.Task.TaskType;

/**
 * Defines how the MandelbrotSet problem is broken up into tasks
 * {@link api.Task Task} and later, combines the solutions for subdivided tasks
 * into solution for the overall problem
 * 
 * @author Vijayaraghavan Subbaiah
 */

public class MandelbrotSetJob implements Job {

	double lowerX;
	double lowerY;
	double edgeLength;
	int numRowSquares;
	int numColumnSquares;
	int iterationLimit;
	int[][] counts;

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
	 */

	MandelbrotSetJob(double lowerX, double lowerY, double edgeLength,
			int numRowSquares, int numColumnSquares, int iterationLimit) {
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.edgeLength = edgeLength;
		this.numRowSquares = numRowSquares;
		this.numColumnSquares = numColumnSquares;
		this.iterationLimit = iterationLimit;
		counts = new int[numRowSquares][numColumnSquares];
	}

	/**
	 * Defines how the MandelbrotSet problem is broken up into tasks
	 * {@link api.Task Task} by clients.
	 * 
	 * @param space
	 *            Represents the remote Space to which Tasks {@link api.Task
	 *            Task} are sent for execution.
	 */

	@Override
	public void generateTasks(Space space) throws RemoteException {
		MandelbrotSetTask task = new MandelbrotSetTask(lowerX, lowerY,
				edgeLength, numRowSquares, numColumnSquares, iterationLimit, 0,
				0, TaskType.CHILDTASK, UUID.randomUUID());
		space.put(task);
	}

	/**
	 * Defines how the solutions to subdivided tasks are combined to solution to
	 * overall problem
	 * 
	 * @param space
	 *            Represents the remote Space to which Tasks {@link api.Task
	 *            Task} are sent for execution.
	 */

	@Override
	public void collectResults(Space space) throws RemoteException {
		@SuppressWarnings("unchecked")
		Result<Map<Integer, int[][]>> result = (Result<Map<Integer, int[][]>>) space
				.take();
		counts = result.getTaskReturnValue().get(0);
	}

	/**
	 * Defines a method to get the complete solution to the MandelbrotSet
	 * problem specified by the clients.
	 * 
	 * @return Represents complete result for the MandelbrotSet problem
	 *         specified by clients.
	 */

	@Override
	public int[][] getOverallResult() {
		return counts;
	}
}
