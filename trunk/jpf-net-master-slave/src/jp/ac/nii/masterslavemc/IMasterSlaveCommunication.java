package jp.ac.nii.masterslavemc;

import java.rmi.Remote;
import java.rmi.RemoteException;

interface IMasterSlaveCommunication extends Remote{

	void searchSlave(SearchParamBundle params) throws RemoteException;

	void notifySearchFinished(SearchResultBundle result) throws RemoteException;

	SearchResultBundle getSearchResults() throws RemoteException;

	SearchParamBundle getSearchParams() throws RemoteException;

	void readyToSearch() throws RemoteException;

	void notifyReadyToSearch() throws RemoteException;
	
	void setSlave(IMasterSlaveCommunication slave) throws RemoteException;
	
	void setMaster(IMasterSlaveCommunication master) throws RemoteException;
	
	IMasterSlaveCommunication getSlave() throws RemoteException;
	
	IMasterSlaveCommunication getMaster() throws RemoteException;
}
