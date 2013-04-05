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
	private static final CommAdapter instance = new CommAdapter();
	
	private CommAdapter(MasterSlaveCommunication comm) {
		this.comm = comm;
	}
	
	private CommAdapter() {
		this(MasterSlaveCommunication.getInstance());
	}
	
	public static CommAdapter getInstance(){
		return instance ;
	}

	/**
	 * Slave: tell the master that initialization has completed and search commands can be issued.
	 * 
	 * @throws RemoteException
	 */
	public void notifyReadyToSearch(int stateId) throws RemoteException {
		comm.getMaster().notifyReadyToSearch(stateId);		
	}

	/**
	 * Slave: Waits for search requests to arrive and retrieves the search command received.
	 * 
	 * @return The search parameters received
	 * @throws RemoteException
	 */
	public SearchParamBundle getSearchParams() throws RemoteException {
		return comm.getSearchParams();
	}

	/**
	 * Slave: Notify the master that the search has concluded and transfer the results.
	 * 
	 * @param res Search results
	 * @throws RemoteException
	 */
	public void notifySearchFinished(SearchResultBundle res) throws RemoteException {
		comm.getMaster().notifySearchFinished(res);		
	}

	/**
	 * Master: Transfer the search parameters and notify the slave to initiate search.
	 * 
	 * @param params Search parameters
	 * @throws RemoteException
	 */
	public void searchSlave(SearchParamBundle params) throws RemoteException {
		comm.getSlave().searchSlave(params);
		comm.setSlaveRunning(true);
	}

	/**
	 * Master: Wait for the slave search to complete and retrieve the search results received.
	 * 
	 * @return Search results
	 */
	public SearchResultBundle getSearchResults() {
		return comm.getSearchResults();
	}
	
}
