package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;

public class ChannelQueues extends Hashtable<Channel,Deque<NetworkMessage>> implements Serializable{

	private static final long serialVersionUID = -1824683490106677482L;

	public void deepCopy(ChannelQueues obj) {
		this.clear();
		for (java.util.Map.Entry<Channel, Deque<NetworkMessage>>  e : obj.entrySet()) {
			Deque<NetworkMessage> q = e.getValue();
			Deque<NetworkMessage> newQ = new LinkedList<NetworkMessage>();
			for (NetworkMessage m : q){
				newQ.addFirst(m);
			}
			this.put(e.getKey(), newQ);
		}
		
	}

}
