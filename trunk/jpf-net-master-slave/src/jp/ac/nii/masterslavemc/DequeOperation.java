package jp.ac.nii.masterslavemc;

import java.io.Serializable;

public class DequeOperation<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8652222451725054564L;

	public enum OP_CODE {
			REMOVE_LAST,
			REMOVE_FIRST,
			WRITE
	}
	
	private int depth;
	private OP_CODE opcode;

	private T ref;

	public DequeOperation(OP_CODE opcode, int depth, T ref) {
		this.opcode = opcode;
		this.depth = depth;
		this.ref = ref;
	}

	public int getDepth() {
		return depth;
	}
	public OP_CODE getOpcode() {
		return opcode;
	}
	
	public T getRef() {
		return ref;
	}

	public DequeOperation<T> deepClone() {
		// Sorry for this :-(
		// TODO: Find a way to really clone the internal reference
		return new DequeOperation<T>(opcode, depth, ref);
	}
}
