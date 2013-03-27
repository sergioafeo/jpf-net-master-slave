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
		boolean depthLimitReached = false;

	    depth = 0;

	    notifySearchStarted();

	    while (!done) {
	      if (checkAndResetBacktrackRequest() || !isNewState() || isEndState() || isIgnoredState() || depthLimitReached ) {
	        if (!backtrack()) { // backtrack not possible, done
	          break;
	        }

	        depthLimitReached = false;
	        depth--;
	        notifyStateBacktracked();
	      }

	      if (forward()) {
	        depth++;
	        notifyStateAdvanced();

	        if (currentError != null){
	          notifyPropertyViolated();

	          if (hasPropertyTermination()) {
	            break;
	          }
	          // for search.multiple_errors we go on and treat this as a new state
	          // but hasPropertyTermination() will issue a backtrack request
	        }

	        if (depth >= depthLimit) {
	          depthLimitReached = true;
	          notifySearchConstraintHit("depth limit reached: " + depthLimit);
	          continue;
	        }

	        if (!checkStateSpaceLimit()) {
	          notifySearchConstraintHit("memory limit reached: " + minFreeMemory);
	          // can't go on, we exhausted our memory
	          break;
	        }

	      } else { // forward did not execute any instructions
	        notifyStateProcessed();
	      }
	    }

	    notifySearchFinished();
		
		
		
		
		try {
		CommAdapter comm = CommAdapter.getInstance();
		
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
