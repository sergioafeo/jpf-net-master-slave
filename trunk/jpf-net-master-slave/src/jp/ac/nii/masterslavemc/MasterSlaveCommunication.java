package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.State;

import java.io.Serializable;

/**
 * This is the communication layer between master and slave model checkers, implements the remote 
 * interface IMasterSlaveCommunication to be used for RMI calls.
 * 
 * @author Sergio A. Feo
 */
class MasterSlaveCommunication implements IMasterSlaveCommunication, Serializable {

	private static final long serialVersionUID = 3953833501640741810L;
	private static final MasterSlaveCommunication instance = new MasterSlaveCommunication();
	private static IMasterSlaveCommunication master, slave;

	/**
	 * @return The singleton instance of MasterSlaveCommunication
	 */
	public static MasterSlaveCommunication getInstance() {
		return instance;
	}
	
	private MasterSlaveCommunication() {};

	private boolean slaveRunning = false;
	
	protected void setSlaveRunning(boolean slaveRunning) {
		this.slaveRunning = slaveRunning;
	}

	private boolean paramsAvailable = false;
	private SearchParamBundle searchParams;

	/* (non-Javadoc)
	 * @see jp.ac.nii.masterslavemc.IMasterSlaveCommunication#searchSlave(jp.ac.nii.masterslavemc.SearchParamBundle)
	 */
	@Override
	public synchronized void searchSlave(SearchParamBundle params) {
		while (paramsAvailable) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		slaveRunning = true;
		paramsAvailable = true;

		searchParams = params;

		notifyAll();
	}

	SearchResultBundle searchResult;

	/* (non-Javadoc)
	 * @see jp.ac.nii.masterslavemc.IMasterSlaveCommunication#notifySearchFinished(jp.ac.nii.masterslavemc.SearchResultBundle)
	 */
	@Override
	public synchronized void notifySearchFinished(SearchResultBundle result) {
//		while (resultsPending)
//			try {
//				wait();
//			} catch (InterruptedException e) {
//			}	
		searchResult = result;
		slaveRunning = false;
//		resultsPending = true;
		notifyAll();
	}

	/* (non-Javadoc)
	 * @see jp.ac.nii.masterslavemc.IMasterSlaveCommunication#getSearchResults()
	 */
	@Override
	public synchronized SearchResultBundle getSearchResults() {
		while (slaveRunning) { 
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		SearchResultBundle retval = searchResult;
//		resultsPending = false;
		return retval;
	}

	/* (non-Javadoc)
	 * @see jp.ac.nii.masterslavemc.IMasterSlaveCommunication#getSearchParams()
	 */
	@Override
	public synchronized SearchParamBundle getSearchParams() {
		while (!paramsAvailable)
			try {
				wait();
			} catch (InterruptedException e) {
			}
		SearchParamBundle retval = searchParams;
		paramsAvailable = false;
		notifyAll();
		return retval;
	}

	private boolean slaveInitializing = true;
	private int slaveState;;

	/* (non-Javadoc)
	 * @see jp.ac.nii.masterslavemc.IMasterSlaveCommunication#readyToSearch()
	 */
	@Override
	public synchronized int readyToSearch() {
		while (slaveInitializing)
			try {
				wait();
			} catch (InterruptedException e) {
			}
		return slaveState;
	}

	/* (non-Javadoc)
	 * @see jp.ac.nii.masterslavemc.IMasterSlaveCommunication#notifyReadyToSearch()
	 */
	@Override
	public synchronized void notifyReadyToSearch(int stateId) {
		slaveState = stateId;
		slaveInitializing = false;
		notifyAll();
	}
	
	@Override
	public void setSlave(IMasterSlaveCommunication slave) {
		MasterSlaveCommunication.slave = slave;
	}
	
	@Override
	public void setMaster(IMasterSlaveCommunication master) {
		MasterSlaveCommunication.master = master;
	}

	public IMasterSlaveCommunication getSlave() {
		return slave;
	}

	public IMasterSlaveCommunication getMaster() {
		return master;
	}
}
