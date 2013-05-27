package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.util.JPFLogger;

/**
 * Simple listener to maintain the network layer queues synchronized with the search.
 * 
 * @author Sergio A. Feo
 *
 */
public class NetworkLayerListener extends ListenerAdapter {
	private transient JPFLogger log = JPF
			.getLogger("jp.ac.nii.masterslavemc.NetworkLayerListener");

	private static boolean saveNextState;
	private static boolean stopSearch;
	private static int stateId;
	private static int num = 0;
	private NetworkLayer net = NetworkLayer.getInstance();

	@Override
	public void stateAdvanced(Search search) {
		net.advance(search.getDepth());
		if (saveNextState){			
			net.updateState(stateId,search.getVM().getRestorableState());
			if (stopSearch) search.getVM().ignoreState();
			stopSearch = false;
			saveNextState = false;
			//log.warning("Stored "+(++num)+" States.");
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
