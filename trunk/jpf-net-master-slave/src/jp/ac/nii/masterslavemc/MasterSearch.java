package jp.ac.nii.masterslavemc;

import jp.ac.nii.masterslavemc.SlaveSearch.SearchCommand;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.JVM;

/**
 * @author Sergio A. Feo
 *
 * Search class for the System under Test, largely based on the standard JPF 
 * depth-first search. It extends its functionality by catching network accesses
 * and triggering slave searches when appropriate.
 */
public class MasterSearch extends SharedSearch {
	
	
	public MasterSearch(Config config, JVM vm) {
		super(config, vm);
	}

	@Override
	public void search() {
		
		notifySearchStarted();
		comm.searchSlave(new SearchParamBundle(null, null, null,
				SearchCommand.SEARCH));
		SearchResultBundle res = comm.getSearchResults();
		System.out.println("Search returned!!");
		notifySearchFinished();
		comm.searchSlave(new SearchParamBundle(null, null, null,
				SearchCommand.FINISH));
	}
}
