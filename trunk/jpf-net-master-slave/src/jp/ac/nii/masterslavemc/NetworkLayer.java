package jp.ac.nii.masterslavemc;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jp.ac.nii.masterslavemc.Channel.ChannelType;

public class NetworkLayer extends ChannelQueues {

	private static final long serialVersionUID = -5989864108646342444L;

	// Singleton
	private static final NetworkLayer instance = new NetworkLayer();

	private NetworkLayer() {
	};

	public static NetworkLayer getInstance() {
		return instance;
	}

	private int slaveState;

	private boolean slave = false;
	
	private SearchParamBundle searchParams;

	public void newChannel(ChannelType socketType, int socketID) {
		// TODO: decide what the best concrete implementation of queues is.
		// for now a LinkedList seems the most compact in memory
		this.put(new Channel(socketType, socketID),
				new LinkedList<NetworkMessage>());
	}

	public void newChannel(Channel c) {
		this.put(c, new LinkedList<NetworkMessage>());
	}

	/**
	 * Accepts a socket connection if there is a connection request on the
	 * slave, for that, the current queue status is queried, or we trigger a
	 * slave search on the port specified.
	 * 
	 * @param port
	 *            Port number of the ServerSocket
	 * @return the socketID of the remote socket that initiated a connection
	 */
	public Set<NetworkMessage> accept(int port) {
		SearchParamBundle params = new SearchParamBundle(slaveState, 
				this.getChannel(port, ChannelType.SERVER), true,
				(ChannelQueues) this, SearchCommand.SEARCH);
		try {
			CommAdapter.getInstance().searchSlave(params);
			SearchResultBundle results = CommAdapter.getInstance()
					.getSearchResults();
			if (results!=null && !results.getSearchResults().isEmpty()) {
				return results.getSearchResults().keySet();
			}
			return null;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public int getSlaveState() {
		return slaveState;
	}

	public void setSlaveState(int slaveState) {
		this.slaveState = slaveState;
	}

	private synchronized Channel getChannel(int id, ChannelType type) {
		Channel c = new Channel(type, id);
		if (this.containsKey(c))
			return c;
		else
			return null;
	}

	Map<NetworkMessage, ChannelQueues> searchResults;
	
	public Map<NetworkMessage, ChannelQueues> getSearchResults(){
		return searchResults;
	}

	public SearchParamBundle getSearchParams() {
		return searchParams;
	}

	public void setSearchParams(SearchParamBundle searchParams) {
		this.searchParams = searchParams;
	}

	public boolean isSlave() {
		return slave;
	}

	/**
	 * Set to true to configure the network layer as slave.
	 * 
	 * @param slave
	 */
	public void setSlave(boolean slave) {
		this.slave = slave;
	}
}
