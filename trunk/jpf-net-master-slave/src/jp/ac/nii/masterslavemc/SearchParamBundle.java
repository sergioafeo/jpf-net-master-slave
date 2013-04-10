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
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SearchParamBundle){
			SearchParamBundle x = (SearchParamBundle) obj;
			if (	x.incomingQueues.equals(this.incomingQueues) &&
					x.command == this.command &&
					x.connectAction == this.connectAction &&
					x.searchChannel.equals(this.searchChannel) &&
					x.startState == this.startState)
				return true;
		}
		return false;
	}

	public SearchCommand getCommand() {
		return command;
	}

	public ChannelQueues getIncomingQueues() {
		return incomingQueues;
	}

	public Channel getSearchChannel() {
		return searchChannel;
	}

	public int getStartState() {
		return startState;
	}

	@Override
	public int hashCode() {
		return incomingQueues.hashCode() +
				command.hashCode() * 73 +
				(connectAction ? 1 : 0) * 547 +
				searchChannel.hashCode() * 1087 +
				startState * 2131;
	}

	public boolean isConnectAction() {
		return connectAction;
	}

	public void setCommand(SearchCommand command) {
		this.command = command;
	}

	public void setConnectAction(boolean connectAction) {
		this.connectAction = connectAction;
	}

	public void setIncomingQueues(ChannelQueues incomingQueues) {
		this.incomingQueues = incomingQueues;
	}

	public void setSearchChannel(Channel searchChannel) {
		this.searchChannel = searchChannel;
	}

	public void setStartState(int startState) {
		this.startState = startState;
	}

}
