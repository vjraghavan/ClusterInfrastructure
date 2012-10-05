package client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

import system.Shared;
import tasks.EuclideanTspTask;
import tasks.TSPResult;
import tasks.TspUpperBound;
import api.Result;
import api.Space;
import api.Task.TaskType;

/**
 * Defines how the Travelling Salesman problem is broken up into tasks
 * {@link api.Task Task} and later, combines the solutions for subdivided tasks
 * into solution for the overall problem
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class EuclideanTspJob implements Job {
	int[] minTour;
	double minDistance;
	double[][] cities;

	EuclideanTspJob(double[][] cities) {
		this.cities = cities.clone();
		minDistance = Double.MAX_VALUE;
	}

	/**
	 * Defines how the TSP problem is broken up into tasks {@link api.Task Task}
	 * by clients.
	 * 
	 * @param space
	 *            Represents the remote Space to which Tasks {@link api.Task
	 *            Task} are sent for execution.
	 */

	@Override
	public void generateTasks(Space space) throws RemoteException {
		ArrayList<Integer> citiesList = new ArrayList<Integer>();
		for (int i = 1; i < cities.length; i++)
			citiesList.add(i);
		int start = 0;
		int next = 0;
		double totDist = 0;
		while (citiesList.size() != 0) {
			double minDist = Double.MAX_VALUE;
			double curDist = 0;
			Iterator<Integer> iter = citiesList.iterator();
			int i = 0;
			int index = 0;
			while (iter.hasNext()) {
				next = iter.next();
				curDist = euclideanDistance(cities[start][0], cities[start][1],
						cities[next][0], cities[next][1]);
				if (curDist < minDist) {
					minDist = curDist;
					index = i;
				}
				i++;
			}
			start = citiesList.remove(index);
			totDist += minDist;
		}
		totDist += euclideanDistance(cities[start][0], cities[start][1],
				cities[0][0], cities[0][1]);
		System.out.println(totDist);
		Shared<Double> tspSharedObj = new TspUpperBound(totDist);
		EuclideanTspTask partialEuclideanTspTask = new EuclideanTspTask(0,
				cities, TaskType.CHILDTASK);
		@SuppressWarnings("unchecked")
		Result<TSPResult> result = (Result<TSPResult>) space.compute(
				partialEuclideanTspTask, tspSharedObj);
		TSPResult tspResult = result.getTaskReturnValue();
		minTour = tspResult.getMinTour();
	}

	private double euclideanDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
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
		Result<TSPResult> result = (Result<TSPResult>) space.take();
		TSPResult tspResult = result.getTaskReturnValue();
		minTour = tspResult.getMinTour();
	}

	/**
	 * Defines a method to get the complete solution to the TSP problem
	 * specified by the clients.
	 * 
	 * @return Represents complete result for the TSP problem specified by
	 *         clients.
	 */

	@Override
	public int[] getOverallResult() {
		for (int i = 0; i < minTour.length; i++) {
			System.out.println(minTour[i]);
		}
		return minTour;
	}
}
