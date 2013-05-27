package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.RestorableVMState;

import java.rmi.RemoteException;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.ac.nii.masterslavemc.Channel.ChannelType;
import jp.ac.nii.masterslavemc.NetworkLayerOperation.OpCode;

public class NetworkLayer {

	// Singleton
	private static final NetworkLayer instance = new NetworkLayer();
	
	public static NetworkLayer getInstance() {
		return instance;
	}

	Map<Integer, Map<NetworkMessage, ChannelQueues>> alternatives = new HashMap<Integer, Map<NetworkMessage, ChannelQueues>>();

	private boolean branchingstate;

	private Map<NetworkMessage, ChannelQueues> currentAlternatives;;

	private int currentDepth = 0;

	// The queues
	private ChannelQueues queues = new ChannelQueues();

	private transient JPFLogger log = JPF
			.getLogger("jp.ac.nii.masterslavemc.NetworkLayer");

	private SearchParamBundle searchParams;

	Map<NetworkMessage, ChannelQueues> searchResults = new HashMap<NetworkMessage, ChannelQueues>();

	private boolean slave = false;
	private int slaveState;
	int stateID;
	private Map<Integer, RestorableVMState> stateRepository = new HashMap<Integer, RestorableVMState>();

	private NetworkLayer() {
		log.info("Network layer initialized.");
	}

	/**
	 * Accepts a socket connection if there is a connection request on the
	 * slave, for that, the current queue status is queried, or we trigger a
	 * slave search on the port specified.
	 * 
	 * @param env
	 *            MJI environment of the current execution
	 * 
	 * @param port
	 *            Port number of the ServerSocket
	 * @return the socketID of the remote socket that initiated a connection
	 */
	public Set<NetworkMessage> accept(MJIEnv env, int port) {
		log.info("Accept:" + port);
		DoubleQueue channelQ = queues.get(Channel.get(ChannelType.SERVER, port));
		assert (channelQ != null) : "Accept message for an uninitialized server socket.";
		BacktrackableDeque<NetworkMessage> Q = channelQ.getIncoming();
		
		if (!slave && Q.isEmpty()) { // Queue is empty, ask the slave
			SearchParamBundle params = new SearchParamBundle(slaveState,
					Channel.get(ChannelType.SERVER, port), true, queues,
					SearchCommand.SEARCH,currentDepth);
			try {
				CommAdapter.getInstance().searchSlave(params);
				SearchResultBundle results = CommAdapter.getInstance()
						.getSearchResults();
				log.info("Found "
						+ (results.getSearchResults() != null ? results
								.getSearchResults().size() : "no")
						+ " connections.");
				if (results.getSearchResults() != null
						&& !results.getSearchResults().isEmpty()) {
					// Get the first message
					Entry<NetworkMessage, ChannelQueues> firstmsg = results
							.getSearchResults().entrySet().iterator().next();
					// Replace our current state with the incoming state
					this.mergeIncoming(firstmsg.getValue());
					slaveState = firstmsg.getKey().getState();
					Set<NetworkMessage> retval = new HashSet<NetworkMessage>(
					 results.getSearchResults().keySet());
					// Remove from the set and save the rest
					results.getSearchResults().remove(firstmsg.getKey());
					// Save if there is something left
					if (results.getSearchResults().size() > 0) {
						branchingstate = true; // Raise the flag to make sure
												// the state is remembered
						this.currentAlternatives = new HashMap<NetworkMessage, ChannelQueues>(
								results.getSearchResults());
						slaveState = firstmsg.getKey().getState();
						// consume the message in the queue, remember to refresh the references
						channelQ = queues.get(Channel.get(ChannelType.SERVER, port));
						Q = channelQ.getIncoming();
						Q.removeLast(currentDepth);
						return retval;
					}
				}
				// return null;
			} catch (RemoteException e) {
				e.printStackTrace();
				return null;
			}
		}
		// At this point we may check again if there is a connect message in the
		// queue
		// We'll come here directly if we are in the slave or we already have
		// data in the queue
		channelQ = queues.get(Channel.get(ChannelType.SERVER, port));
		Q = channelQ.getIncoming();
		if (!Q.isEmpty()) { // There is a connection, return it
			Set<NetworkMessage> res = new HashSet<NetworkMessage>();
			NetworkMessage msg = Q.removeLast(currentDepth);
			res.add(msg);
			slaveState = msg.getState();
			return res;
		}
		return null;
	}

	public int addState(RestorableVMState s) {
		int id = stateRepository.size();
		stateRepository.put(id, s);
		return id;
	}

	/**
	 * Callback method for state advance. (unused)
	 * 
	 * @param depth
	 *            The current depth
	 */
	public void advance(int depth) {
		currentDepth = depth;
		if (branchingstate) {
			alternatives.put(depth, currentAlternatives);
			branchingstate = false;
		}
	}

	/**
	 * Remove queue elements up to a specified depth for backtracking
	 * 
	 * @param depth
	 *            The depth to which it should be backtracked
	 */
	public void backtrack(int depth) {

		// If there are alternatives, switch to the next one
		Map<NetworkMessage, ChannelQueues> alt = alternatives.get(depth);
		if (alt != null && !alt.isEmpty()) {
			java.util.Map.Entry<NetworkMessage, ChannelQueues> newstate = alt
					.entrySet().iterator().next();
			this.mergeIncoming(newstate.getValue());
			slaveState = newstate.getKey().getState();
			alt.remove(newstate.getKey());
		}
		
		// Backtrack the queues individually
		for (Entry<Channel, DoubleQueue> e : queues
				.entrySet()) {
			e.getValue().getOutgoing().backtrack(depth);
			e.getValue().getIncoming().backtrack(depth);
		}
		
		// Backtrack the queue states themselves
		while (!history.isEmpty() && 
				history.peekLast().getDepth() >= depth){
			NetworkLayerOperation op = history.removeLast();
			switch (op.getOpcode()){
			case ADD_CHANNEL:
				queues.remove(op.getChannel()); break;
			case REMOVE_CHANNEL:
				queues.put(op.getChannel(), op.getRef());
			}
		}

		currentDepth = depth;
	}

	public void connect(MJIEnv env, int id, int port) {
		log.info("Connect: " + port);
		// Get the queue for the current socket
		DoubleQueue dq = queues.get(Channel.get(ChannelType.SERVER,
				port));
		assert dq!=null : "Trying to connect to non-existing server socket.";
		BacktrackableDeque<NetworkMessage> Q = dq.getOutgoing();
		// Remember this state, make room for it in the repository
		int stateId = addState(null);
		// Create new CONNECT network message and put in on the corresponding
		// queue
		NetworkMessage msg = new NetworkMessage(0, true, Channel.get(
				ChannelType.CLIENT, id), stateId);
		msg.setDepth(currentDepth);
		Q.add(msg, currentDepth);
		// Check whether this connect is search relevant
		if (slave
				&& searchParams.getSearchChannel().getType() == ChannelType.SERVER
				&& port == searchParams.getSearchChannel().getId()) {
			// Copy the current queue status
			ChannelQueues snapshot = new ChannelQueues();
			// DEEP CLONE NECESSARY
			snapshot.deepCopy(this.queues);
			searchResults.put(msg, snapshot);
			// Tell the listener to stop the search
			env.getVM().breakTransition();
			NetworkLayerListener.saveState(stateId, true);
		}
	}

//	private synchronized Channel getChannel(int id, ChannelType type) {
//		Channel c = Channel.get(type, id);
//		if (outgoing.containsKey(c))
//			return c;
//		else
//			return null;
//	}

	public SearchParamBundle getSearchParams() {
		return searchParams;
	}

	public Map<NetworkMessage, ChannelQueues> getSearchResults() {
		Map<NetworkMessage, ChannelQueues> retval = searchResults;
		searchResults = new HashMap<NetworkMessage, ChannelQueues>();
		return retval;
	}

	public int getSlaveState() {
		return slaveState;
	}

	public RestorableVMState getState(int startState) {
		return stateRepository.get(startState);
	}

	public boolean isSlave() {
		return slave;
	}

	private void mergeIncoming(ChannelQueues in) {
		queues.clear();
		for (Entry<Channel, DoubleQueue> e : in.entrySet()) {
			// Invert incoming and outgoing
			queues.put(e.getKey(), new DoubleQueue(e.getValue().getOutgoing(),
					e.getValue().getIncoming()));
		}
	}

	public boolean newChannel(Channel c) {
		if (queues.get(c) != null)
			return false;
		DoubleQueue ref = new DoubleQueue();
		queues.put(c, ref);
		history.addLast(new NetworkLayerOperation(OpCode.ADD_CHANNEL, currentDepth, c, ref));
		return true;
	}

	public boolean newChannel(ChannelType socketType, int socketID) {
		Channel c = Channel.get(socketType, socketID);
		return newChannel(c);
	}

	public Set<NetworkMessage> read(MJIEnv env, int sockID) throws Exception {
		log.info("Read:" + sockID);
		// Get the queue for the specified socket
		DoubleQueue dq = queues.get(Channel.get(ChannelType.CLIENT,
				sockID));
		BacktrackableDeque<NetworkMessage> Q = dq.getIncoming();
		if (!slave && Q.isEmpty()) { // Need to search the slave
			SearchParamBundle params = new SearchParamBundle(slaveState,
					Channel.get(ChannelType.CLIENT, sockID), false,
					queues, SearchCommand.SEARCH, currentDepth);
			try {
				CommAdapter.getInstance().searchSlave(params);
				SearchResultBundle results = CommAdapter.getInstance()
						.getSearchResults();
				if (results.getSearchResults() != null
						&& !results.getSearchResults().isEmpty()) {
					// Get the first message
					java.util.Map.Entry<NetworkMessage, ChannelQueues> firstmsg = results
							.getSearchResults().entrySet().iterator().next();
					// Replace our current state with the incoming state
					this.mergeIncoming(firstmsg.getValue());
					 Set<NetworkMessage> retval = new HashSet<NetworkMessage>(results.getSearchResults().keySet());
					// Remove from the set and save the rest
					results.getSearchResults().remove(firstmsg.getKey());
					// Save if there is something left
					if (results.getSearchResults().size() > 0) {
						branchingstate = true; // Raise the flag to make sure
												// the state is remembered
						this.currentAlternatives = new HashMap<NetworkMessage, ChannelQueues>(
								results.getSearchResults());
						slaveState = firstmsg.getKey().getState();
						// consume the message, refresh the queue references first
						dq = queues.get(Channel.get(ChannelType.CLIENT,
								sockID));
						Q = dq.getIncoming();
						Q.removeLast(currentDepth);
						return retval;
					}
				}
				// return null;
			} catch (RemoteException e) {
				e.printStackTrace();
				return null;
			}
		}

		// Just retrieve the queue message
		dq = queues.get(Channel.get(ChannelType.CLIENT,
				sockID));
		Q = dq.getIncoming();
		if (!Q.isEmpty()) {
			Set<NetworkMessage> res = new HashSet<NetworkMessage>();
			slaveState = Q.peek().getState();
			res.add(Q.remove(currentDepth));
			return res;
		}
		log.info("Empty read.");
		return null;
	}

	public void setSearchParams(SearchParamBundle searchParams) {
		this.searchParams = searchParams;
		this.mergeIncoming(searchParams.getQueues());
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

	public void setSlaveState(int slaveState) {
		this.slaveState = slaveState;
	}

	public void updateState(int stateId, RestorableVMState restorableState) {
		stateRepository.put(stateId, restorableState);
	}

	public void write(MJIEnv env, int sockID, int b) {
		log.info("WRITE:" + sockID + ": " + b);
		DoubleQueue dq = queues.get(Channel.get(ChannelType.CLIENT,
				sockID));
		assert (dq != null) : "Attempt to write to an uninitialized socket.";
		BacktrackableDeque<NetworkMessage> Q = dq.getOutgoing();
		
		// We may Remember this state, make room for it in the repository
		int stateId = addState(null);
		Channel sock = Channel.get(ChannelType.CLIENT, sockID);
		// Create the message
		NetworkMessage msg = new NetworkMessage(b, false, sock, stateId);
		msg.setDepth(currentDepth);
		Q.add(msg, currentDepth);
		// Check if it is search relevant
		if (slave
				&& searchParams.getSearchChannel().getType() == ChannelType.CLIENT
				&& sock.getId() == searchParams.getSearchChannel().getId()) {
			// Copy the current queue status
			ChannelQueues snapshot = new ChannelQueues();
			snapshot.deepCopy(this.queues);
			searchResults.put(msg, snapshot);
			// Tell the listener to save the state and stop the search
			env.getVM().breakTransition();
			NetworkLayerListener.saveState(stateId, true);
		}
	}

	/**
	 * Checks whether a channel is part of the network state.
	 * 
	 * @param sock_id
	 * @return true if there is a channel with the indicated sock_id
	 */
	public boolean channelOpen(int sock_id) {
		return queues.containsKey(Channel.get(ChannelType.CLIENT, sock_id));
	}

	Deque<NetworkLayerOperation> history = new LinkedList<NetworkLayerOperation>();
	
	/**
	 * Removes the indicated channel from the network state.
	 * 
	 * @param sock_id
	 */
	public void closeSocket(int sock_id) {
		Channel c = Channel.get(ChannelType.CLIENT, sock_id);
		DoubleQueue ref = queues.remove(c);
		history.addLast(new NetworkLayerOperation(NetworkLayerOperation.OpCode.REMOVE_CHANNEL, currentDepth, c, ref));
	}
}
