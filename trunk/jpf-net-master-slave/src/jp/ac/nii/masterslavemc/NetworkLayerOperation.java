package jp.ac.nii.masterslavemc;

public class NetworkLayerOperation {
	public enum OpCode {
		ADD_CHANNEL,
		REMOVE_CHANNEL
	}
	
	private OpCode opcode;
	public OpCode getOpcode() {
		return opcode;
	}

	public void setOpcode(OpCode opcode) {
		this.opcode = opcode;
	}

	private int depth;
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	private Channel c;
	private DoubleQueue ref;
	
	public NetworkLayerOperation(OpCode opcode, int depth,
			Channel c, DoubleQueue ref) {
		this.opcode = opcode;
		this.depth = depth;
		this.setChannel(c);
		this.setRef(ref);
	}

	public Channel getChannel() {
		return c;
	}

	public void setChannel(Channel c) {
		this.c = c;
	}

	public DoubleQueue getRef() {
		return ref;
	}

	public void setRef(DoubleQueue ref) {
		this.ref = ref;
	}
}
