package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.jvm.RestorableVMState;
import jp.ac.nii.masterslavemc.SlaveSearch.SearchCommand;

public class SearchParamBundle {
	private RestorableVMState startState;
	private Channel searchChannel;
	private ChannelQueues incomingQueues;
	private SearchCommand command;

	public SearchParamBundle(RestorableVMState startState, Channel searchChannel,
			ChannelQueues incomingQueues, SearchCommand command) {
		this.startState = startState;
		this.searchChannel = searchChannel;
		this.incomingQueues = incomingQueues;
		this.command = command;
	}

	public RestorableVMState getStartState() {
		return startState;
	}

	public void setStartState(RestorableVMState startState) {
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

}
