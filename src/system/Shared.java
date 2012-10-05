package system;

/**
 * An interface which abstracts properties of an object that is shared among all
 * of a computation's unfinished tasks by the compute space. Its value, when
 * changed by any task, is propagated with best effort to all unfinished tasks
 * by the compute space.
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 */
public interface Shared<T> {
	/**
	 * 
	 * @param existingShared
	 *            Shared object to compare
	 * @return Returns true if the argument is older than this shared object
	 */
	boolean isNewerThan(Shared<?> existingShared);

	/**
	 * 
	 * @return Returns the object that is being shared
	 */
	T get();
}