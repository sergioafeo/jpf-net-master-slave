package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.MJIEnv;
import gov.nasa.jpf.jvm.RestorableVMState;
import gov.nasa.jpf.util.JPFLogger;

import java.rmi.RemoteException;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
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

	int stateID;
	Map<Integer, Map<NetworkMessage, ChannelQueues>> alternatives = new HashMap<Integer, Map<NetworkMessage, ChannelQueues>>();
	private Map<NetworkMessage, ChannelQueues> currentAlternatives;
	private int currentDepth;
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
		Deque<NetworkMessage> Q = this.get(this.getChannel(port,
				ChannelType.SERVER));
		assert (Q!=null) : "Accept message for an uninitialized server socket.";
		if (!slave && Q.isEmpty() ) { // Queue is empty, ask the slave
			SearchParamBundle params = new SearchParamBundle(slaveState,
					this.getChannel(port, ChannelType.SERVER), true,
					(ChannelQueues) this, SearchCommand.SEARCH);
			try {
				CommAdapter.getInstance().searchSlave(params);
				SearchResultBundle results = CommAdapter.getInstance()
						.getSearchResults();
				if (results.getSearchResults() != null
						&& !results.getSearchResults().isEmpty()) {
					// Get the first message
					java.util.Map.Entry<NetworkMessage, ChannelQueues> firstmsg = results.getSearchResults().entrySet().iterator().next();
					// Add it to the queue
					Q.add(firstmsg.getKey());
					this.merge(firstmsg.getValue());
					// Remove from the set and save the rest
					results.getSearchResults().remove(firstmsg.getKey());
					// Save if there is something left
					if (results.getSearchResults().size() > 0) {
						branchingstate = true;
						this.currentAlternatives = new HashMap<NetworkMessage, ChannelQueues>(
								results.getSearchResults());
					}
					return results.getSearchResults().keySet(); 
				}
				return null;
			} catch (RemoteException e) {
				e.printStackTrace();
				return null;
			}
		}
		// At this point we may check again if there is a connect message in the queue
		// We'll come here directly if we are in the slave or we already have data in the queue
		if (!Q.isEmpty()){ // There is a connection, return it
			Set<NetworkMessage> res = new HashSet<NetworkMessage>();
			res.add(Q.removeLast());
			return res;
		}
		return null;
	}

	private void merge(ChannelQueues queues) {
		for (java.util.Map.Entry<Channel,Deque<NetworkMessage>> q : queues.entrySet()){
			Deque<NetworkMessage> localqueue = this.get(q.getKey());
			Deque<NetworkMessage> remotequeue = q.getValue();
			
			if (remotequeue.isEmpty()) continue;
			
			boolean found = false;
			NetworkMessage localTail = localqueue.peekLast();
			NetworkMessage remoteMsg = remotequeue.removeFirst();
			while (!localTail.equals(remoteMsg)){
				remoteMsg = remotequeue.removeFirst(); 
			}
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
	private boolean branchingstate;
	
	public Map<NetworkMessage, ChannelQueues> getSearchResults(){
		Map<NetworkMessage, ChannelQueues> retval = searchResults;
		searchResults = new HashMap<NetworkMessage, ChannelQueues>();
		return retval;
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
		
		// Check whether this connect is search relevant
		if (slave && searchParams.getSearchChannel().getType() == ChannelType.SERVER && port == searchParams.getSearchChannel().getId()){						
				// Copy the current queue status
				ChannelQueues queues = new ChannelQueues();
				queues.putAll(this);
				searchResults.put(msg, queues);
				// Tell the listener to save the state and stop the search
				NetworkLayerListener.saveState(stateId, true);
		} else // Just add it to the search and continue
			Q.add(msg);
	}

	/**
	 * Callback method for state advance. (unused)
	 * @param depth The current depth
	 */
	public void advance(int depth) {
		currentDepth = depth;
		if (branchingstate){
			alternatives.put(depth, currentAlternatives);
			branchingstate = false;
		}
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
		if (!alternatives.get(depth).isEmpty()){
			
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
