package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import tasks.TaskContainer;

import api.Result;
import api.Space;
import api.Task;
import api.SuccessorTask;

/**
 * Represents a computing resource which defines how ({@link api.Task Task}) are
 * automatically executed by registered workers as soon as they are dropped into
 * the {@link api.Space Space}.
 * 
 * @author Vijayaraghavan Subbaiah
 */

public class SpaceImpl extends UnicastRemoteObject implements Space,
		Computer2Space, Runnable {
	public final String SERVICE_NAME = "Space";
	LinkedBlockingDeque<Task<?>> readyTaskList;
	LinkedBlockingQueue<Result<?>> resultList;
	ConcurrentMap<UUID, SuccessorTask<?>> waitingSuccessorsMap;
	ConcurrentHashMap<Computer, ComputerProxy> registeredComputerList;
	LinkedBlockingQueue<Result<?>> finalResult;
	Shared<?> shared = null;
	Thread t;
	private Registry registry;
	SpaceExecutor spaceExecutor;

	public SpaceImpl() throws RemoteException {
		super();
		readyTaskList = new LinkedBlockingDeque<Task<?>>();
		resultList = new LinkedBlockingQueue<Result<?>>();
		registeredComputerList = new ConcurrentHashMap<Computer, ComputerProxy>();
		waitingSuccessorsMap = new ConcurrentHashMap<UUID, SuccessorTask<?>>();
		finalResult = new LinkedBlockingQueue<Result<?>>();
		t = new Thread(this);
		t.start();
		spaceExecutor = new SpaceExecutor(this);
	}

	private static final long serialVersionUID = 2L;

	@Override
	public String register(Computer computer) throws RemoteException {
		String cpServiceName = UUID.randomUUID().toString();
		ComputerProxy cp = new ComputerProxy(computer, this, cpServiceName,
				getRegistry());
		registeredComputerList.put(computer, cp);
		return cpServiceName;
	}

	public void removeComputer(Computer computer) throws RemoteException {
		registeredComputerList.remove(computer);
	}

	/**
	 * Defines how the client decomposes the problem into a set of
	 * {@link api.Task Task} objects, and passes them to the {@link api.Space
	 * Space}
	 * 
	 * @param task
	 *            Represents {@link api.Task Task} to be executed.
	 * 
	 * @throws java.rmi.RemoteException
	 *             Throws RemoteException when there is a problem in writing the
	 *             tasks to the remote Compute Space
	 */

	@Override
	public void put(Task<?> task) throws java.rmi.RemoteException {
		try {
			readyTaskList.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Defines how the client retrieves the associated {@link api.Result Result}
	 * objects via the take method. This method blocks until a result is
	 * available to return to the client.
	 * 
	 * @return Returns the associated {@link api.Result Result} objects for the
	 *         submitted {@link api.Task Task} Objects.
	 * @throws java.rmi.RemoteException
	 *             Throws RemoteException when there is a problem in reading the
	 *             results from the remote Compute Space
	 */

	public Result<?> takeResult() throws RemoteException {
		try {
			return (Result<?>) resultList.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void putFinalResult(Result<?> result) throws RemoteException {
		finalResult.add(result);
	}

	public Result<?> take() throws RemoteException {
		try {
			return finalResult.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Defines a deployment convenience to stop each registered
	 * {@link system.Computer Computer} and then stops itself.
	 * 
	 * @throws java.rmi.RemoteException
	 *             Throws RemoteException when registered computers are stopped.
	 */

	@Override
	public void shutdown() throws RemoteException {
		Iterator<Computer> iter = registeredComputerList.keySet().iterator();
		while (iter.hasNext()) {
			try {
				registeredComputerList.get(iter.next()).shutdown();
			} catch (Exception e) {
				continue;
			}
		}
		registeredComputerList.clear();
		java.util.Date date = new java.util.Date();
		System.out.println("Compute Space goes down now at :"
				+ new Timestamp(date.getTime()));
		System.exit(0);
	}

	/**
	 * Defines how the {@link system.ComputerProxy ComputerProxy} takes the
	 * associated {@link api.Task Task} objects for execution by remote
	 * {@link system.Computer Computer}.
	 * 
	 * @return Returns the {@link api.Task Task} to be executed in remote
	 *         {@link system.Computer Computer}.
	 */

	public Task<?> takeTask() throws RemoteException {
		try {
			return readyTaskList.takeLast();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Defines how the {@link system.ComputerProxy ComputerProxy} puts the
	 * associated {@link api.Result Result} objects into the {@link api.Space
	 * Space}.
	 */

	public void putResult(Result<?> result) throws RemoteException {
		try {
			resultList.put(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void putWaitingSuccessor(UUID key, Task<?> task)
			throws RemoteException {
		waitingSuccessorsMap.put(key, (SuccessorTask<?>) task);
	}

	public Task<?> getWaitingSuccessor(UUID key) throws RemoteException {
		return waitingSuccessorsMap.get(key);
	}

	public void removeSuccessor(UUID key) throws RemoteException {
		waitingSuccessorsMap.remove(key);
	}

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		Space space;
		try {
			space = new SpaceImpl();
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.rebind(Space.SERVICE_NAME, space);
			space.setRegistry(registry);
			System.out.println("Space Instance is bound and ready");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			ResultImpl<?> partialResult;
			while (true) {
				partialResult = (ResultImpl<?>) resultList.take();
				if (partialResult.getLevelNumber() == -1) {
					try {
						putFinalResult(partialResult);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} else {
					UUID successorID = partialResult.getSuccessorID();
					SuccessorTask<?> sTask = (SuccessorTask<?>) getWaitingSuccessor(successorID);
					sTask.putArguments(partialResult);
					if (sTask.getJointCounter() == 0) {
						try {
							removeSuccessor(successorID);
							spaceExecutor.putIntoExecutor(sTask);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Space Exception");
		}
	}

	@Override
	public void setShared(Shared<?> proposedShared) throws RemoteException {
		if (this.shared == null) {
			shared = proposedShared;
			Iterator<Computer> iterator = registeredComputerList.keySet()
					.iterator();
			while (iterator.hasNext()) {
				try {
					// iterator.next().setShared(shared, true);
					registeredComputerList.get(iterator.next())
							.setSharedBySpace(shared, true);
				} catch (RemoteException e) {
					System.out.println("Machine Down");
					continue;
				}
			}
		} else {
			if (proposedShared.isNewerThan(shared)) {
				shared = proposedShared;
				Thread t = new Thread(new Runnable() {
					public void run() {
						Iterator<Computer> iterator = registeredComputerList
								.keySet().iterator();
						while (iterator.hasNext()) {
							try {
								// iterator.next().setShared(shared, true);
								registeredComputerList.get(iterator.next())
										.setSharedBySpace(shared, true);
							} catch (RemoteException e) {
								System.out.println("Machine Down");
								continue;
							}
						}
					}
				});
				t.start();
			}
		}
	}

	@Override
	public Result<?> compute(Task<?> task, Shared<?> shared)
			throws RemoteException {
		setShared(shared);
		try {
			put(task);
			return take();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public void putPrunedResult(UUID successorID) {
		try {
			SuccessorTask<?> sTask = (SuccessorTask<?>) getWaitingSuccessor(successorID);
			sTask.putArguments(null);
			if (sTask.getJointCounter() == 0) {
				removeSuccessor(successorID);
				// put(sTask);
				spaceExecutor.putIntoExecutor(sTask);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getServiceName() throws RemoteException {
		return SERVICE_NAME;
	}

	public Registry getRegistry() {
		return registry;
	}

	@Override
	public void setRegistry(Registry registry) throws RemoteException {
		this.registry = registry;
	}

	@Override
	public void putGeneratedTasks(TaskContainer tc, UUID taskID) {
	}

	@Override
	public void putResults(Result<?> result, UUID taskID) {
	}
}
