package tasks;

import java.util.LinkedList;

/**
 * A general purpose stack which is efficiently useful for doing DFS
 * computations
 * 
 * @author Vijayaraghavan Subbaiah
 * 
 * @param <T>
 *            Represents the type of the underlying data present in the stack
 */
public class Stack<T> {
	LinkedList<T> stack = new LinkedList<T>();

	/**
	 * Validates whether the stack is empty or not
	 * 
	 * @return Returns true if stack is empty and false if it is not empty
	 */
	boolean isEmpty() {
		return (stack.size() == 0) ? true : false;
	}

	/**
	 * Removes the top element from the stack. LIFO principle is used in Stack
	 * 
	 * @return Returns the top element in the stack.
	 */
	T pop() {
		assert !isEmpty();
		return stack.removeFirst();
	}

	/**
	 * Inserts the element on the top of the stack
	 * 
	 * @param task
	 *            Represents the task to be computed
	 */
	void push(T task) {
		stack.addFirst(task);
	}
}
