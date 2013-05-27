package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RemoteSlave {

	public static void main(String[] args) {
		System.err.println("Initializing JPF...");
		Config configSlave = JPF.createConfig(args);

		// Set the configuration parameters
		configSlave.setProperty("search.class",
				"jp.ac.nii.masterslavemc.SlaveSearch");
		configSlave.setProperty("target",
				"jp.ac.nii.masterslavemc.tests.NondetSlave");
		configSlave.setProperty("target.args", "2");
		// Obtain JPF instance
		JPF slave = new JPF(configSlave);

		// Configure the local network layer
		NetworkLayer.getInstance().setSlave(true);
				
		// Start the communication
		// Get the RMI infrastructure up and running, connect to the master
		try {
			System.err.println("Connecting to master...");
			Registry registry = LocateRegistry.getRegistry("localhost");
			IMasterSlaveCommunication stub = (IMasterSlaveCommunication) registry
					.lookup("RemoteMasterSearch");
			MasterSlaveCommunication.getInstance().setMaster(stub);
			MasterSlaveCommunication
					.getInstance()
					.getMaster()
					.setSlave(
							(IMasterSlaveCommunication) UnicastRemoteObject
									.exportObject(MasterSlaveCommunication
											.getInstance(), 0));
			System.err.println("Connected to master, launching JPF...");
			// Launch JPF
			slave.run();
		} catch (RemoteException e1) {
			e1.printStackTrace();

		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.gc();
			System.runFinalization();
			System.exit(0);
		}
	}
}
