package tasks;

import java.io.Serializable;
import java.util.LinkedList;

import api.SuccessorTask;
import api.Task;

public class TaskContainer implements Serializable {
	private static final long serialVersionUID = -8636812964597333133L;
	private SuccessorTask<?> successorTask;
	private LinkedList<Task<?>> childTaskList;

	/**
	 * A complex Object containing both the list of subtasks and also its
	 * associated successor
	 * 
	 * @param successorTask
	 *            Task which combines the results returned by the sub tasks
	 * @param childTaskList
	 *            Represents the generated list of sub tasks
	 */
	public TaskContainer(SuccessorTask<?> successorTask,
			LinkedList<Task<?>> childTaskList) {
		super();
		this.successorTask = successorTask;
		this.childTaskList = childTaskList;
	}

	/**
	 * Represents the hashCode of this java object
	 */
	public int hashCode() {
		int hashTour = successorTask != null ? successorTask.hashCode() : 0;
		int hashDistance = childTaskList != null ? childTaskList.hashCode() : 0;
		return (hashTour + hashDistance) * hashDistance + hashTour;
	}

	/**
	 * Overrides the java object's equal method
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof TaskContainer) {
			TaskContainer otherPair = (TaskContainer) other;
			return ((this.successorTask == otherPair.successorTask || (this.successorTask != null
					&& otherPair.successorTask != null && this.successorTask
						.equals(otherPair.successorTask))) && (this.childTaskList == otherPair.childTaskList || (this.childTaskList != null
					&& otherPair.childTaskList != null && this.childTaskList
						.equals(otherPair.childTaskList))));
		}
		return false;
	}

	/**
	 * Overrides the java Object's toString() method
	 */
	@Override
	public String toString() {
		return "(" + successorTask + ", " + childTaskList + ")";
	}

	/**
	 * Gets the successor task which will combine the results of the results of
	 * the subtasks
	 * 
	 * @return Returns the successor task which will combine the results of the
	 *         results of the subtasks
	 */
	public SuccessorTask<?> getSuccessorTask() {
		return successorTask;
	}

	/**
	 * Gets the list of child tasks generated
	 * 
	 * @return Returns the list of child tasks
	 */
	public LinkedList<Task<?>> getChildTaskList() {
		return childTaskList;
	}
}
