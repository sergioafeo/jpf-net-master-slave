package jp.ac.nii.masterslavemc;

import java.rmi.Remote;
import java.rmi.RemoteException;

interface IMasterSlaveCommunication extends Remote{

	void searchSlave(SearchParamBundle params) throws RemoteException;

	void notifySearchFinished(SearchResultBundle result) throws RemoteException;

	SearchResultBundle getSearchResults() throws RemoteException;

	SearchParamBundle getSearchParams() throws RemoteException;

	/**
	 * @return the stateId of the starting state of the slave.
	 * @throws RemoteException
	 */
	int readyToSearch() throws RemoteException;

	void notifyReadyToSearch(int id) throws RemoteException;
	
	void setSlave(IMasterSlaveCommunication slave) throws RemoteException;
	
	void setMaster(IMasterSlaveCommunication master) throws RemoteException;
	
	IMasterSlaveCommunication getSlave() throws RemoteException;
	
	IMasterSlaveCommunication getMaster() throws RemoteException;
}
