package jp.ac.nii.masterslavemc;

import java.rmi.RemoteException;
import java.util.Map;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.RestorableVMState;

public class SlaveSearch extends SharedSearch {

	public SlaveSearch(Config config, JVM vm) {
		super(config, vm);
	}

	@Override
	public void search() {
		CommAdapter comm = CommAdapter.getInstance();
		stateMap = comm.getStateMap();
		SearchParamBundle params;

		boolean done = false;
		
		try {	
			// Store the initial state in our map
			stateMap.put(vm.getStateId(), vm.getRestorableState());
			comm.notifyReadyToSearch(vm.getStateId());
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

	private Map<Integer, RestorableVMState> stateMap;
	
	/**
	 * Do a normal DFS search after restoring the VM to the state specified by the master.
	 * The search finishes when a write operation on a specified channel is found.
	 * 
	 * @param params Search parameters
	 * @return The set of network messages
	 */
	private SearchResultBundle doSearch(SearchParamBundle params) {
				
		NetworkLayer net = NetworkLayer.getInstance();
		int StartState = params.getStartState();
		boolean depthLimitReached = false;
		
		// Tell the network layer where to stop searching
		net.setSearchParams(params);
		// TODO: Backtrack to the starting state
		if (stateMap.get(StartState) != null)
			vm.restoreState(stateMap.get(StartState));
		else
			throw new JPFException("Master specified an inexistent state:" + StartState);
		done  = false;
		// Search
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
		
		SearchResultBundle results = new SearchResultBundle(net.getSearchResults());
		return results;
	}
}
