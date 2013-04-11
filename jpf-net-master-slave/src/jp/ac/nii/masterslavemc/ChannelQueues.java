package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.Deque;
import java.util.Hashtable;

public class ChannelQueues extends Hashtable<Channel,Deque<NetworkMessage>> implements Serializable{

	private static final long serialVersionUID = -1824683490106677482L;

}
