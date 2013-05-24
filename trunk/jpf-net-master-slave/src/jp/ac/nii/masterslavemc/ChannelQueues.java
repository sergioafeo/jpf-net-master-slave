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
			BacktrackableDeque<NetworkMessage> newIncoming = e.getValue().getIncoming().deepClone();
			BacktrackableDeque<NetworkMessage> newOutgoing = e.getValue().getOutgoing().deepClone();
			this.put(e.getKey(), new DoubleQueue(newIncoming, newOutgoing));
		}
		
	}

}
