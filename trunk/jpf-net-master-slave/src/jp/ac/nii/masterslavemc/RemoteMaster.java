package jp.ac.nii.masterslavemc;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.search.Search;

public class RemoteMaster {

	public static void main(String[] args) {
		Config configMaster = JPF.createConfig(args);
		// Config configSlave = JPF.createConfig(args);

		// Set the configuration parameters for master and slave
		configMaster.setProperty("search.class",
				"jp.ac.nii.masterslavemc.MasterSearch");
		configMaster.setProperty(Config.TARGET_KEY,
				"jp.ac.nii.masterslavemc.examples.SimpleMaster");

		// Obtain JPF instance
		JPF master = new JPF(configMaster);

		// Start the communication
		// Get the RMI infrastructure up and running, export the comm object
		try {			
			IMasterSlaveCommunication stub = (IMasterSlaveCommunication) UnicastRemoteObject
					.exportObject(MasterSlaveCommunication.getInstance(), 0);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("RemoteMasterSearch", stub);
			System.err
					.println("Master server started. Waiting for the slave to initialize...");
		} catch (RemoteException e1) {
			e1.printStackTrace();
			
			return;
		}
		// Finally, launch the master after waiting for the slave to initialize
		try {
			while (MasterSlaveCommunication.getInstance().getSlave() == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			System.err.println("Slave connected, awaiting notification...");
			MasterSlaveCommunication.getInstance().readyToSearch();
			System.err.println("Slave ready, launching JPF...");
			master.run();
			LocateRegistry.getRegistry().unbind("RemoteMasterSearch");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			assert(false) : "Something really is spooky: the remote object was unbound, and it wasn't me.";
		}
		finally { // die gracefully
			System.gc();
		    System.runFinalization();
			System.exit(0);
		}
	}
}
