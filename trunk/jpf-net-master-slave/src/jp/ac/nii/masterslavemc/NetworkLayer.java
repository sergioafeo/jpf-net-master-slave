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
	
	public void newChannel(Channel c){
		this.put(c, new LinkedList<NetworkMessage>());
	}
	
	/**
	 * Accepts a socket connection if there is a connection request on the slave, for that, 
	 * the current queue status is queried, or we trigger a slave search on the port specified. 
	 * @param port Port number of the ServerSocket
	 * @return true if the connection was accepted
	 */
	public boolean accept(int port) {
		// TODO Auto-generated method stub
		return false;
	}
}
