package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Class that constitutes the main store for the network state. It consists of two queues:
 * One for incoming and one for outgoing messages. It maintains a history of operations
 * to ensure full persistency during search. 
 * 
 * @author Sergio A. Feo
 *
 */
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

	private BacktrackableDeque<NetworkMessage> incoming, outgoing;
	
	public DoubleQueue(){
		incoming = new BacktrackableDeque<NetworkMessage>();
		outgoing = new BacktrackableDeque<NetworkMessage>();
	}

	public DoubleQueue(BacktrackableDeque<NetworkMessage> newIncoming,
			BacktrackableDeque<NetworkMessage> newOutgoing) {
		incoming = newIncoming;
		outgoing = newOutgoing;
	}
	
	public BacktrackableDeque<NetworkMessage> getIncoming() {
		return incoming;
	}

	public BacktrackableDeque<NetworkMessage> getOutgoing() {
		return outgoing;
	}

	public void invert(){
		BacktrackableDeque<NetworkMessage> temp = incoming;
		incoming = outgoing;
		outgoing = temp;
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
