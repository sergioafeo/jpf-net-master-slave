package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Queue;

public class ChannelQueues extends HashMap<Channel,Queue<NetworkMessage>> implements Serializable{

	private static final long serialVersionUID = -1824683490106677482L;

}
