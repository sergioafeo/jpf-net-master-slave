package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.Map;

public class SearchResultBundle implements Serializable {

	private static final long serialVersionUID = 7081722154221103965L;
	private Map<NetworkMessage,ChannelQueues> searchResults;

	public SearchResultBundle(Map<NetworkMessage, ChannelQueues> searchResults) {
		super();
		this.searchResults = searchResults;
	}

	public Map<NetworkMessage, ChannelQueues> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(Map<NetworkMessage, ChannelQueues> searchResults) {
		this.searchResults = searchResults;
	}
	
}
