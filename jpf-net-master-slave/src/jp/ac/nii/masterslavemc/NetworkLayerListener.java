package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.search.Search;

/**
 * Simple listener to maintain the network layer queues synchronized with the search.
 * 
 * @author Sergio A. Feo
 *
 */
public class NetworkLayerListener extends ListenerAdapter {
	private static boolean saveNextState;
	private static boolean stopSearch;
	private static int stateId;
	private NetworkLayer net = NetworkLayer.getInstance();

	@Override
	public void stateAdvanced(Search search) {
		net.advance(search.getDepth());
		if (saveNextState){			
			net.updateState(stateId,search.getVM().getRestorableState());
			if (stopSearch) search.getVM().ignoreState();
			stopSearch = false;
			saveNextState = false;
		}
	}

	@Override
	public void stateBacktracked(Search search) {	
		net.backtrack(search.getDepth());
	}

	public static void saveState(int id, boolean stop) {
		saveNextState = true;
		stateId = id;
		stopSearch = stop;
	}

}
