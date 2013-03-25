package jp.ac.nii.masterslavemc;

import java.util.List;

public class SearchResultBundle {
	private ChannelQueues queues;
	private List<NetworkMessage> searchResults;
	public ChannelQueues getQueues() {
		return queues;
	}
	public List<NetworkMessage> getSearchResults() {
		return searchResults;
	}
	public void setQueues(ChannelQueues queues) {
		this.queues = queues;
	}
	public void setSearchResults(List<NetworkMessage> searchResults) {
		this.searchResults = searchResults;
	}
}
