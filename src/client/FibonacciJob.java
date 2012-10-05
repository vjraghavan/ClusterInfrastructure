package client;

import java.util.UUID;
import java.rmi.RemoteException;

import tasks.FibonacciTask;

import api.Result;
import api.Space;
import api.Task.TaskType;

public class FibonacciJob implements Job {
	int n;
	int sum;
	int levelNumber;

	public FibonacciJob(int n) {
		this.n = n;
		levelNumber = 0;
		sum = 0;
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
		FibonacciTask task = new FibonacciTask(levelNumber, n,
				TaskType.CHILDTASK, UUID.randomUUID());
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
		Result<Integer> result = (Result<Integer>) space.take();
		sum = result.getTaskReturnValue();
	}

	/**
	 * Defines a method to get the complete solution to the MandelbrotSet
	 * problem specified by the clients.
	 * 
	 * @return Represents complete result for the MandelbrotSet problem
	 *         specified by clients.
	 */

	@Override
	public Integer getOverallResult() {
		return sum;
	}
}
