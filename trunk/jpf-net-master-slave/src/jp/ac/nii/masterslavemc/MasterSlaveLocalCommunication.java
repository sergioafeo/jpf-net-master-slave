package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.JPF;

/**
 * This is the communication layer between master and slave model
 * checkers
 * @author Sergio A. Feo
 */
public class MasterSlaveLocalCommunication {
	private JPF master, slave;
	private static final MasterSlaveLocalCommunication instance = new MasterSlaveLocalCommunication();

	public static MasterSlaveLocalCommunication getInstance() {
		return instance;
	}

	public static void setModelCheckerRefs(JPF master, JPF slave) {
		instance.master = master;
		instance.slave = slave;
	}

	private boolean slaveRunning = false, resultsPending = false,
			paramsAvailable = false;
	private SearchParamBundle searchParams;

	public synchronized void searchSlave(SearchParamBundle params) {
		while (slaveRunning || paramsAvailable) {
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

	public synchronized void notifySearchFinished(SearchResultBundle result) {
		searchResult = result;
		slaveRunning = false;
		resultsPending = true;
		notifyAll();
	}

	public synchronized SearchResultBundle getSearchResults() {
		while (slaveRunning || !resultsPending) { // Wait for the slave to
													// finish before searching
													// again, should NEVER
													// happen
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		SearchResultBundle retval = searchResult;
		resultsPending = false;
		notifyAll();
		return retval;
	}

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

	private boolean slaveInitializing = true;;

	public synchronized void readyToSearch() {
		while (slaveInitializing)
			try {
				wait();
			} catch (InterruptedException e) {
			}
	}

	public synchronized void notifyReadyToSearch() {
		slaveInitializing = false;
		notifyAll();
	}
}
