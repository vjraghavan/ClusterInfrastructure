package system;

import tasks.TaskContainer;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Task;
import api.TaskImpl;

/**
 * Defines the Compute Engine which executes resource intensive tasks
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class ComputerImpl extends UnicastRemoteObject implements Computer,
		Runnable {

	private static final long serialVersionUID = -1090559231989990343L;
	Computer2Space spaceProxy;
	Shared<?> shared = null;
	private Thread[] tarr;
	LinkedBlockingQueue<Task<?>> readyTaskList;

	public ComputerImpl() throws RemoteException {
		super();
		readyTaskList = new LinkedBlockingQueue<Task<?>>();
	}

	void setSpaceProxy(Computer2Space spaceProxy) {
		this.spaceProxy = spaceProxy;
		tarr = new Thread[Runtime.getRuntime().availableProcessors()];
		for (int i = 0; i < tarr.length; i++) {
			tarr[i] = new Thread(this);
			tarr[i].start();
		}
	}

	/**
	 * Executes the tasks submitted by the clients
	 * 
	 * @param t
	 *            Represents the task submitted to Space
	 * @return Result of the execution of the task
	 */

	@Override
	public Result<?> execute(Task<?> t) throws RemoteException {
		((TaskImpl<?>) t).setComputer(this);
		Result<?> result = t.execute();
		return result;
	}

	@Override
	public void shutdown() throws RemoteException {
		java.util.Date date = new java.util.Date();
		System.out.println("Computer goes down now at :"
				+ new Timestamp(date.getTime()));
		System.exit(0);
	}

	public static void main(String[] args) {

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String spaceMachineName = args[0];
			Registry registry = LocateRegistry.getRegistry(spaceMachineName);
			Computer2Space comp2Space = (Computer2Space) registry
					.lookup("Space");
			ComputerImpl compImpl = new ComputerImpl();
			String spaceProxyServiceName = comp2Space.register(compImpl);
			Computer2Space spaceProxy = (Computer2Space) registry
					.lookup(spaceProxyServiceName);
			compImpl.setSpaceProxy(spaceProxy);
			System.out.println("Computer is bound and ready ");
		} catch (Exception e) {
			System.err.println("ComputeEngine exception:");
			e.printStackTrace();
		}
	}

	@Override
	public LinkedList<Task<?>> generateTasks(Task<?> t, UUID successorID)
			throws RemoteException {
		((TaskImpl<?>) t).setComputer(this);
		return t.generateTasks(successorID, this);
	}

	@Override
	public Result<?> composeResult(Task<?> t) throws RemoteException {
		((TaskImpl<?>) t).setComputer(this);
		Result<?> partialResult = t.composeResult();
		return partialResult;
	}

	@Override
	public Task<?> generateSuccessorTask(Task<?> partialTask, int jointCounter) {
		((TaskImpl<?>) partialTask).setComputer(this);
		try {
			return partialTask.generateSuccessorTask(jointCounter);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setShared(Shared<?> proposedShared, boolean bySpace)
			throws RemoteException {
		if (bySpace) {
			shared = proposedShared;
		} else {
			if (proposedShared.isNewerThan(shared)) {
				shared = proposedShared;
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							spaceProxy.setShared(shared);
						} catch (RemoteException e) {
							System.out.println("Machine Down");
						}
					}
				});
				t.start();
			}
		}
	}

	public Shared<?> getShared() {
		return shared;
	}

	@Override
	public TaskContainer generate(Task<?> partialTask, UUID successorID)
			throws RemoteException {
		return partialTask.generate(successorID, this);
	}

	@Override
	public void putTasksInComputer(Task<?> task)
			throws java.rmi.RemoteException {
		try {
			readyTaskList.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void executeTask(final Task<?> partialTask) throws RemoteException {
		try {
			long startTime = System.nanoTime();
			if (partialTask.isDecomposable()) {
				UUID successorID = UUID.randomUUID();
				final TaskContainer tc = generate(partialTask, successorID);
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							spaceProxy.putGeneratedTasks(tc,
									partialTask.getTaskID());
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				});
				t.start();
			} else {
				final Result<?> partialResult = execute(partialTask);
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							spaceProxy.putResults(partialResult,
									partialTask.getTaskID());
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				});
				t.start();
			}
			long endTime = System.nanoTime();
			long elapsedTime = endTime - startTime;
			System.out.println(elapsedTime);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Task<?> partialTask = null;
		while (true) {
			try {
				partialTask = readyTaskList.take();
				executeTask(partialTask);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
