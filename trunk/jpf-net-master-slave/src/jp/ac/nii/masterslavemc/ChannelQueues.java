package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;

public class ChannelQueues extends Hashtable<Channel,DoubleQueue> implements Serializable{

	private static final long serialVersionUID = -1824683490106677482L;

	public void deepCopy(ChannelQueues obj) {
		this.clear();
		for (java.util.Map.Entry<Channel, DoubleQueue>  e : obj.entrySet()) {
			DoubleQueue q = e.getValue();
			Deque<NetworkMessage> newIncoming = new LinkedList<NetworkMessage>();
			Deque<NetworkMessage> newOutgoing = new LinkedList<NetworkMessage>();
			for (NetworkMessage m : q.getIncoming()){
				newIncoming.addFirst(m);
			}
			for (NetworkMessage m : q.getOutgoing()){
				newOutgoing.addFirst(m);
			}
			this.put(e.getKey(), new DoubleQueue(newIncoming, newOutgoing));
		}
		
	}

}
