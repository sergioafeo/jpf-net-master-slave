package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.State;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;

import jp.ac.nii.masterslavemc.Channel.ChannelType;

public class NetworkLayer extends ChannelQueues{

	private static final long serialVersionUID = -5989864108646342444L;

	// Singleton
	private static final NetworkLayer instance = new NetworkLayer();
	private NetworkLayer() {};
	public static NetworkLayer getInstance() { return instance; }
	private State slaveState;
	
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
		SearchParamBundle params = new SearchParamBundle(slaveState, this.getChannel(port, ChannelType.SERVER), (ChannelQueues)this, SearchCommand.SEARCH);
		try {
			CommAdapter.getInstance().searchSlave(params);
			SearchResultBundle results = CommAdapter.getInstance().getSearchResults();
			if (!results.getSearchResults().get(this.getChannel(port, ChannelType.SERVER)).isEmpty()){
				
				return true;
			}
			return false;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public State getSlaveState() {
		return slaveState;
	}
	public void setSlaveState(State slaveState) {
		this.slaveState = slaveState;
	}
	private synchronized Channel getChannel(int id, ChannelType type) {
		Channel c = new Channel(type, id);
		if (this.containsKey(c))
			return c;
		else
			return null;
	}
	
}