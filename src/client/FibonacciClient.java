package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import api.Space;

public class FibonacciClient {

	public static void main(String args[]) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String spaceMachineName = args[0];
			Registry registry = LocateRegistry.getRegistry(spaceMachineName);
			Space space = (Space) registry.lookup(Space.SERVICE_NAME);
			long startTime = System.nanoTime();
			FibonacciJob fibonacciJob = new FibonacciJob(20);
			fibonacciJob.generateTasks(space);
			fibonacciJob.collectResults(space);
			int sum = fibonacciJob.getOverallResult();
			long endTime = System.nanoTime();
			System.out.println("Fibonacci of 20 is :" + sum);
			System.out.println(" Total runTime as seen by the Client :"
					+ (endTime - startTime));
		} catch (Exception e) {
			System.err.println("Compute exception:");
			e.printStackTrace();
		}
	}

}
