package system;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import tasks.TaskContainer;
import api.Result;
import api.SuccessorTask;
import api.Task;

/**
 * Represents a thread taking {@link api.Task tasks} from the {@link api.Space
 * Space} and sends it to the registered {@link system.Computer computers} to
 * execute it remotely and place the {@link api.Result Result} back into the
 * Space to be fetched by the clients.
 * 
 * @author Vijayaraghavan Subbaiah
 */

public class ComputerProxy extends UnicastRemoteObject implements Runnable,
		Computer2Space {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2001231189089230248L;
	private Computer computer;
	private SpaceImpl space;
	private Thread t;
	String serviceName = new String();
	ConcurrentHashMap<UUID, Task<?>> cachedTaskList;

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

	public ComputerProxy(Computer computer, SpaceImpl space,
			String serviceName, Registry registry) throws AccessException,
			RemoteException {
		this.computer = computer;
		this.space = space;
		this.serviceName = serviceName;
		cachedTaskList = new ConcurrentHashMap<UUID, Task<?>>();
		t = new Thread(this);
		t.start();
		Registry reg = LocateRegistry.getRegistry();
		reg.rebind(serviceName, this);
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
		Task<?> partialTask = null;
		while (true) {
			try {
				partialTask = space.takeTask();
				cachedTaskList.put(partialTask.getTaskID(), partialTask);
				computer.putTasksInComputer(partialTask);
			} catch (RemoteException e) {
				System.out
						.println("Remote Exception received from the Computer");
				System.out
						.println("Reassigning all the cached tasks back to the Space");
				try {
					Set<Entry<UUID, Task<?>>> entrySet = cachedTaskList
							.entrySet();
					for (Entry<UUID, Task<?>> entry : entrySet) {
						space.put(entry.getValue());
					}
					cachedTaskList.clear();
					space.removeComputer(computer);
					return;
				} catch (RemoteException e1) {
					System.out.println("Remote Exception Received");
				}
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
		computer.shutdown();
		t.interrupt();
	}

	@Override
	public String register(Computer computer) throws RemoteException {
		return null;
	}

	@Override
	public void setShared(Shared<?> shared) throws RemoteException {
		space.setShared(shared);
	}

	@Override
	public String getServiceName() throws RemoteException {
		return null;
	}

	@Override
	public void putGeneratedTasks(TaskContainer tc, UUID taskID) {
		try {
			cachedTaskList.remove(taskID);
			final LinkedList<Task<?>> subTasks;
			SuccessorTask<?> successorTask = tc.getSuccessorTask();
			if (successorTask.getJointCounter() == 0) {
				space.putPrunedResult(successorTask.getSuccessorID());
			} else {
				subTasks = tc.getChildTaskList();
				space.putWaitingSuccessor(subTasks.get(0).getSuccessorID(),
						successorTask);
				Iterator<Task<?>> iterator = subTasks.iterator();
				while (iterator.hasNext()) {
					space.put(iterator.next());
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void putResults(Result<?> result, UUID taskID) {
		try {
			cachedTaskList.remove(taskID);
			space.putResult(result);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void setSharedBySpace(Shared<?> shared, boolean b)
			throws RemoteException {
		computer.setShared(shared, b);
	}
}
