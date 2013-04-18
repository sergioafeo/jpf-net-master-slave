package jp.ac.nii.masterslavemc;

import java.rmi.RemoteException;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.Config;

public class MainLocal {
	private static JPF master, slave;

	/**
	 * @param args
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException {
		Config configMaster = JPF.createConfig(args);
		Config configSlave = JPF.createConfig(args);

		// Set the configuration parameters for master and slave
		configMaster.setProperty("search.class",
				"jp.ac.nii.masterslavemc.MasterSearch");
		configMaster.setProperty("target",
				"jp.ac.nii.masterslavemc.examples.SimpleMaster");
		configSlave.setProperty("search.class",
				"jp.ac.nii.masterslavemc.SlaveSearch");
		configSlave.setProperty("target",
				"jp.ac.nii.masterslavemc.examples.SimpleSlave");
		// Obtain JPF instances
		master = new JPF(configMaster);
		slave = new JPF(configSlave);

		// Start the slave in the background
		(new Thread() {
			public void run() {
				slave.run();
			}
		}).start();
		// Finally, launch the master after waiting for the slave to initialize
		MasterSlaveCommunication.getInstance().readyToSearch();
		master.run();
	}
}
