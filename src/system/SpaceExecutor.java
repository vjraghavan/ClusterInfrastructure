package system;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.concurrent.LinkedBlockingQueue;
import api.Result;
import api.SuccessorTask;

/**
 * Represents a thread taking {@link api.Task tasks} from the {@link api.Space
 * Space} and sends it to the registered {@link system.Computer computers} to
 * execute it remotely and place the {@link api.Result Result} back into the
 * Space to be fetched by the clients.
 * 
 * @author Vijayaraghavan Subbaiah
 */

public class SpaceExecutor implements Runnable {
	private SpaceImpl space;
	private Thread t;
	LinkedBlockingQueue<SuccessorTask<?>> successorTaskList;

	/**
	 * 
	 * @param computer
	 *            Represents the remote {@link system.Computer Computer} ready
	 *            to execute the tasks.
	 * @param space
	 *            Represents the {@link api.Space Space}
	 * @param
	 * @throws RemoteException
	 * @throws AccessException
	 */

	public SpaceExecutor(SpaceImpl space) {
		this.space = space;
		successorTaskList = new LinkedBlockingQueue<SuccessorTask<?>>();
		t = new Thread(this);
		t.start();
	}

	/**
	 * Represents a thread which removes {@link api.Task tasks} from a queue,
	 * invoking the associated {@link system.Computer Computer's} execute method
	 * with the task as its argument, and putting the returned
	 * {@link api.Result Result} back into the {@link api.Space Space} for
	 * retrieval by the client
	 */

	@Override
	public void run() {
		SuccessorTask<?> partialTask = null;
		while (true) {
			try {
				partialTask = successorTaskList.take();
				Result<?> result = partialTask.composeResult();
				space.putResult(result);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Defines a deployment convenience to stop registered Computer.
	 * 
	 * @throws java.rmi.RemoteException
	 *             Throws RemoteException when registered computer is stopped.
	 */

	public void shutdown() throws RemoteException {
		t.interrupt();
	}

	public void putIntoExecutor(SuccessorTask<?> sTask) {
		successorTaskList.add(sTask);
	}
}
