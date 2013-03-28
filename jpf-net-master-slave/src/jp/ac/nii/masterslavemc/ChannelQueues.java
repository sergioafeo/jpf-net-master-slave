package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Queue;

public class ChannelQueues extends Hashtable<Channel,Queue<NetworkMessage>> implements Serializable{

	private static final long serialVersionUID = -1824683490106677482L;

}
