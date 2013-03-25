package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.search.Search;

public class Main {
	private static JPF master, slave;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Config configMaster = JPF.createConfig(args);
		Config configSlave = JPF.createConfig(args);

		// Set the configuration parameters for master and slave
		configMaster.setProperty("search.class",
				"jp.ac.nii.masterslavemc.MasterSearch");
		configMaster.setProperty(Config.TARGET_KEY,
				"jp.ac.nii.masterslavemc.examples.SimpleMaster");
		configSlave.setProperty("search.class",
				"jp.ac.nii.masterslavemc.SlaveSearch");
		configSlave.setProperty(Config.TARGET_KEY,
				"jp.ac.nii.masterslavemc.examples.SimpleSlave");
		// Obtain JPF instances
		master = new JPF(configMaster);
		slave = new JPF(configSlave);
		// Link both instances using the communications interface
		MasterSlaveLocalCommunication.setModelCheckerRefs(master, slave);
		// Start the slave in the background
		(new Thread() {
			public void run() {
				slave.run();
			}
		}).start();
		// Finally, launch the master after waiting for the slave to initialize
		MasterSlaveLocalCommunication.getInstance().readyToSearch();
		master.run();
	}
}
