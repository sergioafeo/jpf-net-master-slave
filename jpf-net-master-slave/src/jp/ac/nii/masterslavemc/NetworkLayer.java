package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.RestorableVMState;
import gov.nasa.jpf.util.JPFLogger;

import java.rmi.RemoteException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import jp.ac.nii.masterslavemc.Channel.ChannelType;

public class NetworkLayer extends ChannelQueues {

	private transient JPFLogger log = JPF.getLogger("jp.ac.nii.masterslavemc.NetworkLayer");
	private static final long serialVersionUID = -5989864108646342444L;

	// Singleton
	private static final NetworkLayer instance = new NetworkLayer();

	private NetworkLayer() {
		log.info("Network layer initialized.");
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
		this.put(Channel.get(socketType, socketID),
				new LinkedList<NetworkMessage>());
	}

	public void newChannel(Channel c) {
		this.put(c, new LinkedList<NetworkMessage>());
	}

	Map<NetworkMessage, ChannelQueues> alternatives = new HashMap<NetworkMessage, ChannelQueues>();
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
			if (results.getSearchResults()!=null && !results.getSearchResults().isEmpty()) {
				// Save the alternatives??
				this.alternatives.putAll(results.getSearchResults());
				return results.getSearchResults().keySet();
			}
			return null;
		} catch (RemoteException e) {
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
		Channel c = Channel.get(type, id);
		if (this.containsKey(c))
			return c;
		else
			return null;
	}

	Map<NetworkMessage, ChannelQueues> searchResults = new HashMap<NetworkMessage, ChannelQueues>();
	private Map<Integer,RestorableVMState> stateRepository = new HashMap<Integer, RestorableVMState>();
	
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
		if (slave)
			log.info("Network layer set to SLAVE mode.");
		else 
			log.info("Network layer set to MASTER mode.");
	}

	public void connect(MJIEnv env, int id, int port) {
		// Get the queue for the current socket
		Deque<NetworkMessage> Q = this.get(Channel.get(ChannelType.CLIENT,id));
		// We may Remember this state, make room for it in the repository
		int stateId = stateRepository.size();
		stateRepository.put(stateId, null);
		// Create new CONNECT network message and put in on the corresponding queue
		NetworkMessage msg = new NetworkMessage(0, true, Channel.get(ChannelType.CLIENT,id), stateId);
		msg.setDepth(env.getJPF().getSearch().getDepth());
		Q.add(msg);
		
		if (slave){			
			// If this is what we were searching for
			if (searchParams.getSearchChannel().getType() == ChannelType.SERVER && port == searchParams.getSearchChannel().getId()){
				// Copy the current queue status
				ChannelQueues queues = new ChannelQueues();
				queues.putAll(this);
				searchResults.put(msg, queues);
				// Tell the listener to save the state and stop the search
				NetworkLayerListener.saveState(stateId, true);
			}
		}
	}

	/**
	 * Callback method for state advance. (unused)
	 * @param depth The current depth
	 */
	public void advance(int depth) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Remove queue elements up to a specified depth for backtracking
	 * @param depth The depth to which it should be backtracked
	 */
	public void backtrack(int depth) {
		// Remove from the queues all messages below the indicated depth
		for (java.util.Map.Entry<Channel, Deque<NetworkMessage>> e : this.entrySet()){
			Deque<NetworkMessage> Q = e.getValue();
			while (Q.peekLast()!=null && Q.peekLast().getDepth() >= depth)
				Q.removeLast();
		}
	}

	public void updateState(int stateId, RestorableVMState restorableState) {
		stateRepository.put(stateId, restorableState);		
	}

	public RestorableVMState getState(int startState) {
		return stateRepository.get(startState);
	}
	
	public int addState(RestorableVMState s) {
		int id = stateRepository.size();
		stateRepository.put(id, s);
		return id;
	}

	public Set<NetworkMessage> read(MJIEnv env, int sockID) {
		// Get the queue for the specified socket
		Deque<NetworkMessage> Q = this.get(Channel.get(ChannelType.CLIENT,sockID));
		if (Q.isEmpty()){ //Need to search the slave
			return null;
		} else {
			return null;
		}
	}
}
