package jp.ac.nii.masterslavemc;

import java.rmi.RemoteException;

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
		try {
		CommAdapter comm = new CommAdapter();
		
		comm.searchSlave(new SearchParamBundle(null, null, null,
				SearchCommand.SEARCH));
		SearchResultBundle res = comm.getSearchResults();
		System.out.println("Search returned!!");
		comm.searchSlave(new SearchParamBundle(null, null, null,
				SearchCommand.FINISH));
		notifySearchFinished();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
