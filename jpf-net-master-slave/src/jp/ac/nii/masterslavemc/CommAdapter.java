package jp.ac.nii.masterslavemc;

import java.rmi.RemoteException;

/**
 * Provides the user API for communicating between master and slave model checkers.
 * Dispatches the method calls to the correct peer (master or slave) according to the
 * method invoked.
 * 
 * @author Sergio A. Feo
 */
public class CommAdapter {
	private MasterSlaveCommunication comm;
	
	private CommAdapter(MasterSlaveCommunication comm) {
		this.comm = comm;
	}
	
	public CommAdapter() {
		this(MasterSlaveCommunication.getInstance());
	}

	public void notifyReadyToSearch() throws RemoteException {
		comm.getMaster().notifyReadyToSearch();		
	}

	public SearchParamBundle getSearchParams() throws RemoteException {
		return comm.getSearchParams();
	}

	public void notifySearchFinished(SearchResultBundle res) throws RemoteException {
		comm.getMaster().notifySearchFinished(res);		
	}

	public void searchSlave(SearchParamBundle params) throws RemoteException {
		comm.getSlave().searchSlave(params);
	}

	public SearchResultBundle getSearchResults() {
		return comm.getSearchResults();
	}
	
}
