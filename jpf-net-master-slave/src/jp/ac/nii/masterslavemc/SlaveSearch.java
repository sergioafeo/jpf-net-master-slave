package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.RestorableVMState;
import gov.nasa.jpf.vm.VM;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

public class SlaveSearch extends SharedSearch {

	private Set<SearchParamBundle> searchCache = new HashSet<SearchParamBundle>();
	
	public SlaveSearch(Config config, VM vm) {
		super(config, vm);
	}

	@Override
	public void search() {
		CommAdapter comm = CommAdapter.getInstance();
		SearchParamBundle params;

		boolean done = false;
		
		try {	
			// Store the initial state in our map
			int startId = NetworkLayer.getInstance().addState(vm.getRestorableState());
			comm.notifyReadyToSearch(startId);
			log.info("Slave search started.");
			notifySearchStarted();
			while (!done)
				try {
					params = comm.getSearchParams();
					switch (params.getCommand()) {
					case SEARCH:
						log.fine("Search request received.");
						SearchResultBundle res = doSearch(params);
						comm.notifySearchFinished(res);		
						log.fine("Search results sent.");
						break;
					case FINISH:
						done = true;
						log.info("Slave search terminated!!");
						break;
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			notifySearchFinished();
		} catch (RemoteException e) {
			e.printStackTrace();			
		} catch(Exception e) {
			log.info("Error caught during search:" + e.getLocalizedMessage());
			try {
				comm.notifySearchFinished(null);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private NetworkLayer net = NetworkLayer.getInstance();
	
	/**
	 * Do a normal DFS search after restoring the VM to the state specified by the master.
	 * The search finishes when a write operation on a specified channel is found.
	 * 
	 * @param params Search parameters
	 * @return The set of network messages
	 */
	private SearchResultBundle doSearch(SearchParamBundle params) {

		// Check whether we have seen this search before
		if (searchCache.contains(params))
			return new SearchResultBundle(null);
		searchCache.add(params);
		int startState = params.getStartState();
		boolean depthLimitReached = false;
		
		// Tell the network layer where to stop searching
		net.setSearchParams(params);
		// Backtrack to the starting state
		RestorableVMState s = net.getState(startState);
		
		depth = 0;
		if (s != null){
			vm.restoreState(s);
//			ChoiceGenerator<?> cg = vm.getChoiceGenerator();
//			if (cg!=null) cg.reset();
			vm.resetNextCG();
			vm.forceState();
		}
		else
			throw new JPFException("Master specified an inexistent state:" + startState);
		boolean restartedSearch = startState != 0;
		done  = false;
		// Search
		while (!done) {
		    //if (!restartedSearch)  
				if ( checkAndResetBacktrackRequest() 
			    		  || (!isNewState() && !restartedSearch) 
			    		  || isEndState() 
			    		  || isIgnoredState() 
			    		  || depthLimitReached ) {
			    	restartedSearch = false;
			        
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
		
		SearchResultBundle results = new SearchResultBundle(net.getSearchResults());
		return results;
	}
}
