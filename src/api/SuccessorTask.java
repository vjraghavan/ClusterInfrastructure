package api;

/**
 * Models any Successor task that can be executed on a remote Compute Engine. A
 * successor Task is the one which collects the results of the tasks and
 * composes into an intermediate Solution. A successor may return the results to
 * its parent successor who will wait on this successor for the results.
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */

public interface SuccessorTask<T> extends Task<T> {

	/**
	 * Represents number of results this successor is waiting for before it can
	 * make progress.
	 * 
	 * @return Returns the number of results needed for the successor to start
	 *         execution.
	 */

	int getJointCounter();

	/**
	 * Gateway for the tasks to submit the results to the successor which is
	 * waiting for the results before it can make progress
	 * 
	 * @param args
	 *            Represents the {@link api.Result} object.
	 */
	void putArguments(Result<?> args);
}
