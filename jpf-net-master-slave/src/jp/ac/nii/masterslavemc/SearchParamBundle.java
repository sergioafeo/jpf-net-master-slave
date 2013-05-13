package jp.ac.nii.masterslavemc;

import java.io.Serializable;

public class SearchParamBundle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6078460271380596549L;

	private int startState;
	private int masterDepth;

	private Channel searchChannel;
	private ChannelQueues queues;
	public void setQueues(ChannelQueues queues) {
		this.queues = queues;
	}

	private SearchCommand command;
	private boolean connectAction;

	public SearchParamBundle(int slaveState, Channel searchChannel,
			boolean connectAction, ChannelQueues queues2,
			SearchCommand command, int masterDepth) {
		this.setConnectAction(connectAction);
		this.startState = slaveState;
		this.searchChannel = searchChannel;
		this.queues = queues2;
		this.command = command;
		this.masterDepth = masterDepth;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SearchParamBundle) {
			SearchParamBundle x = (SearchParamBundle) obj;
			if (x.queues.equals(this.queues)
					&& x.command == this.command
					&& x.connectAction == this.connectAction
					&& x.searchChannel.equals(this.searchChannel)
					&& x.startState == this.startState
			//		&& x.masterDepth == this.masterDepth)
					)
				return true;
		}
		return false;
	}

	public SearchCommand getCommand() {
		return command;
	}

	public ChannelQueues getQueues() {
		return queues;
	}

	public Channel getSearchChannel() {
		return searchChannel;
	}

	public int getStartState() {
		return startState;
	}

	@Override
	public int hashCode() {
		return queues.hashCode() + command.hashCode() * 73
				+ (connectAction ? 1 : 0) * 547 + searchChannel.hashCode()
				* 1087 + startState * 2131;
		//+	masterDepth * 2137;
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

	public void setSearchChannel(Channel searchChannel) {
		this.searchChannel = searchChannel;
	}

	public void setStartState(int startState) {
		this.startState = startState;
	}

}
