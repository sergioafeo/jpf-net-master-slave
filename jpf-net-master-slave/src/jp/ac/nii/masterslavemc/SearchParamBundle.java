package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.Queue;

import gov.nasa.jpf.State;
import gov.nasa.jpf.jvm.RestorableVMState;

public class SearchParamBundle implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6078460271380596549L;
	private int startState;
	private Channel searchChannel;
	private ChannelQueues incomingQueues;
	private SearchCommand command;
	private boolean connectAction;

	public SearchParamBundle(int slaveState, Channel searchChannel, boolean connectAction,
			ChannelQueues incomingQueues, SearchCommand command) {
		this.setConnectAction(connectAction);
		this.startState = slaveState;
		this.searchChannel = searchChannel;
		this.incomingQueues = incomingQueues;
		this.command = command;
	}

	public int getStartState() {
		return startState;
	}

	public void setStartState(int startState) {
		this.startState = startState;
	}

	public Channel getSearchChannel() {
		return searchChannel;
	}

	public void setSearchChannel(Channel searchChannel) {
		this.searchChannel = searchChannel;
	}

	public ChannelQueues getIncomingQueues() {
		return incomingQueues;
	}

	public void setIncomingQueues(ChannelQueues incomingQueues) {
		this.incomingQueues = incomingQueues;
	}

	public SearchCommand getCommand() {
		return command;
	}

	public void setCommand(SearchCommand command) {
		this.command = command;
	}

	public boolean isConnectAction() {
		return connectAction;
	}

	public void setConnectAction(boolean connectAction) {
		this.connectAction = connectAction;
	}

}
