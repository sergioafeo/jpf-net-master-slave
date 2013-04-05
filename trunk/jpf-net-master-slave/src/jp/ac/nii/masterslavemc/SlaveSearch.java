package jp.ac.nii.masterslavemc;

import java.rmi.RemoteException;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.JVM;

public class SlaveSearch extends SharedSearch {

	public SlaveSearch(Config config, JVM vm) {
		super(config, vm);
	}

	@Override
	public void search() {
		try {
			CommAdapter comm = CommAdapter.getInstance();
			SearchParamBundle params;
			boolean done = false;
			comm.notifyReadyToSearch(vm.getStateId());
			notifySearchStarted();
			while (!done)
				try {
					params = comm.getSearchParams();
					switch (params.getCommand()) {
					case SEARCH:
						SearchResultBundle res = doSearch(params);
						comm.notifySearchFinished(res);						
						break;
					case FINISH:
						done = true;
						System.out.println("Slave search terminated!!");
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
		}
	}

	private SearchResultBundle doSearch(SearchParamBundle params) {
		NetworkLayer net = NetworkLayer.getInstance();
		int StartState = params.getStartState();
		boolean depthLimitReached = false;
		
		// Tell the network layer where to stop searching
		net.setSearchParams(params);
		// TODO: Backtrack to the starting state
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
