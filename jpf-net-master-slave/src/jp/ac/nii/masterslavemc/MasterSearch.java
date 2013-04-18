package jp.ac.nii.masterslavemc;

import java.rmi.RemoteException;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.VM;

/**
 * @author Sergio A. Feo
 *
 * Search class for the System under Test, largely based on the standard JPF 
 * depth-first search. It extends its functionality by catching network accesses
 * and triggering slave searches when appropriate.
 */
public class MasterSearch extends SharedSearch {
	
	
	public MasterSearch(Config config, VM vm) {
		super(config, vm);
	}

	@Override
	public void search() {
		CommAdapter comm = CommAdapter.getInstance();
		boolean depthLimitReached = false;

	    depth = 0;

	    System.err.println("Awaiting slave notification...");
		NetworkLayer.getInstance().setSlaveState(MasterSlaveCommunication.getInstance().readyToSearch());
		
		log.info("Slave reported ready. Master search started.");
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
		log.info("Search finished, sending termination command to slave.");
		comm.searchSlave(new SearchParamBundle(0, null, false, null,
				SearchCommand.FINISH));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
