package api;

/**
 * Represents the results of the tasks({@link api.Task Task}) executed on a
 * registered remote {@link system.Computer Computer}
 * 
 * @author Vijayaraghavan Subbaiah
 */

public interface Result<T> extends java.io.Serializable {

	/**
	 * Returns the exact result of the {@link api.Task Task} executed on a
	 * registered remote {@link system.Computer Computer}.
	 * 
	 * @return T Represents the exact result of the {@link api.Task Task}
	 *         executed on a registered remote {@link system.Computer Computer}.
	 */

	T getTaskReturnValue();

}