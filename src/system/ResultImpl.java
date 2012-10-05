package system;

import java.io.Serializable;
import java.util.UUID;

import api.Result;

/**
 * Defines the container which contains results of the {@link api.Task tasks}
 * computed.
 * 
 * @author Vijayaraghavan Subbaiah
 */
public class ResultImpl<T> implements Result<T>, Serializable {

	private static final long serialVersionUID = 4L;
	private int levelNumber;
	private T taskReturnValue;
	UUID successorID;

	/**
	 * 
	 * @param taskReturnValue
	 *            Represents the result of the {@link api.Task task} executed by
	 *            the remote {@link system.Computer Computer}
	 */

	public ResultImpl(int levelNumber, T taskReturnValue, UUID successorID) {
		this.setLevelNumber(levelNumber);
		this.taskReturnValue = taskReturnValue;
		this.successorID = successorID;
	}

	/**
	 * Returns the exact result of the {@link api.Task Task} executed on a
	 * registered remote {@link system.Computer Computer}.
	 * 
	 * @return T Represents the exact result of the {@link api.Task Task}
	 *         executed on a registered remote {@link system.Computer Computer}.
	 */

	@Override
	public T getTaskReturnValue() {
		return taskReturnValue;
	}

	/**
	 * Represents the level of decomposition during the divide phase of the
	 * divide and conquer strategy.
	 * 
	 * @return Returns the level number which identifies the current status of
	 *         decomposition.
	 */

	public int getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}

	/**
	 * Gets an unique ID which identifies the Successor to which
	 * {@link api.Task Task} submitted for computation has to return its
	 * results.
	 * 
	 * @return Returns an unique ID which identifies the Successor for this
	 *         {@link api.Task Task}.
	 */

	public UUID getSuccessorID() {
		return successorID;
	}

}
