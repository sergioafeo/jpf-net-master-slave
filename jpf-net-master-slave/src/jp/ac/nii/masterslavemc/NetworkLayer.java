package jp.ac.nii.masterslavemc;

import java.util.LinkedList;

import jp.ac.nii.masterslavemc.Channel.ChannelType;

public class NetworkLayer extends ChannelQueues{

	private static final long serialVersionUID = -5989864108646342444L;

	// Singleton
	private static final NetworkLayer instance = new NetworkLayer();
	private NetworkLayer() {};
	public static NetworkLayer getInstance() { return instance; }
	
	public void newChannel(ChannelType socketType, int socketID) {
		//TODO: decide what the best concrete implementation of queues is.
		//  for now a LinkedList seems the most compact in memory
		this.put(new Channel(socketType, socketID),new LinkedList<NetworkMessage>());
	}
}
