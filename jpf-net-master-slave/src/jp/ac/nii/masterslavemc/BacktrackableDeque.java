package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Extension of deque to make it backtrackable. Only partially implemented, will implement the Deque
 * interface if deemed necessary.
 * 
 * @author Sergio A. Feo
 *
 * @param <T> Type of the payload of the Deque
 */
public class BacktrackableDeque<T> implements Iterable<T>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9063524591823933771L;

	@Override
	public String toString() {
		return "["+data.toString()+"/"+history.toString()+"]";
	}

	private Deque<T> data;
	private Deque<DequeOperation<T>> history;
	
	public BacktrackableDeque() {
		data = new LinkedList<T>();
		history =  new LinkedList<DequeOperation<T>>();
	}
	public BacktrackableDeque(Deque<T> data, Deque<DequeOperation<T>> history) {
		super();
		this.data = data;
		this.history = history;
	}
	public boolean isEmpty() {
		return data.isEmpty();
	}
	public T removeLast(int depth) {
		T removedRef = data.removeLast();
		history.addLast(new DequeOperation<T>(DequeOperation.OP_CODE.REMOVE_LAST, depth, removedRef));
		return removedRef;
	}
	public T peekLast() {
		return data.peekLast();
	}
	public void add(T x, int depth) {
		data.add(x);
		history.addLast(new DequeOperation<T>(DequeOperation.OP_CODE.WRITE, depth, x));
	}
	public T peek() {
		return data.peek();
	}
	
	public T remove(int depth) {
		T removedRef = data.remove();
		history.addLast(new DequeOperation<T>(DequeOperation.OP_CODE.REMOVE_FIRST, depth, removedRef));
		return removedRef;
	}
	
	public T removeFirst(int depth){
		return remove(depth);
	}
	
	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}
	/**
	 * A not so deep cloning method. You will obtain a new object with new data and history
	 * but not clones of the contents.
	 * 
	 * @return A proper clone of this Deque
	 */
	public BacktrackableDeque<T> deepClone() {
		BacktrackableDeque<T> result = new BacktrackableDeque<T>();
		for (T item : data) {
			result.data.add(item);
		}
		for (DequeOperation<T> item : history) {
			result.history.add(item.deepClone());
		}
		return result;
	}
	
	/**
	 * Restores the deque to the state it was at the indicated depth. Undoes all changes 
	 * stored in the history until the specified depth is reached.
	 * 
	 * @param depth The depth to which the Deque should be backtracked to
	 */
	public void backtrack(int depth){
		while (!history.isEmpty() && history.peekLast().getDepth() >= depth){
			DequeOperation<T> op = history.removeLast();
			// Undo the operation
			switch (op.getOpcode()){
			case WRITE:
				data.remove(); break;
			case REMOVE_FIRST:
				data.addFirst(op.getRef()); break;
			case REMOVE_LAST:
				data.addLast(op.getRef()); break;
			}
		}
	}
}
