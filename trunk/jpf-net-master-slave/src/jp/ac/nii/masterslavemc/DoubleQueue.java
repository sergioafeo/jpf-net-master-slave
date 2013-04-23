package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

public class DoubleQueue implements Serializable{
	@Override
	public int hashCode() {
		return incoming.hashCode()+outgoing.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DoubleQueue){
			DoubleQueue dq = (DoubleQueue) o;
			return dq.getIncoming().equals(incoming) && dq.getOutgoing().equals(outgoing);
		}
		return false;
	}

	private static final long serialVersionUID = 5944112381766159796L;

	private Deque<NetworkMessage> incoming, outgoing;
	
	public DoubleQueue(){
		incoming = new LinkedList<NetworkMessage>();
		outgoing = new LinkedList<NetworkMessage>();
	}

	public DoubleQueue(Deque<NetworkMessage> newIncoming,
			Deque<NetworkMessage> newOutgoing) {
		incoming = newIncoming;
		outgoing = newOutgoing;
	}
	
	public Deque<NetworkMessage> getIncoming() {
		return incoming;
	}

	public Deque<NetworkMessage> getOutgoing() {
		return outgoing;
	}

	public void setIncoming(Deque<NetworkMessage> incoming) {
		this.incoming = incoming;
	}

	public void setOutgoing(Deque<NetworkMessage> outgoing) {
		this.outgoing = outgoing;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("{I:");
		sb.append(incoming.toString());
		sb.append("/O:");
		sb.append(outgoing.toString());
		sb.append("}");
		return sb.toString();
	}
}
