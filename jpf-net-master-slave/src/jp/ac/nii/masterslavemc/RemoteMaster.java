package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import java.net.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RemoteMaster {

	public static void main(String[] args) {
		Config configMaster = JPF.createConfig(args);
		
		// Set the configuration parameters for master and slave
		configMaster.setProperty("search.class",
				"jp.ac.nii.masterslavemc.MasterSearch");
		configMaster.setProperty("target",
				"jp.ac.nii.masterslavemc.examples.SimpleMaster");

		// Obtain JPF instance
		JPF master = new JPF(configMaster);

		// Configure the local network layer
		NetworkLayer.getInstance().setSlave(false);
		
		// Start the communication
		// Get the RMI infrastructure up and running, export the comm object
		IMasterSlaveCommunication stub;
		Registry registry;
		
		try {
			stub = (IMasterSlaveCommunication) UnicastRemoteObject
					.exportObject(MasterSlaveCommunication.getInstance(), 0);
		} catch (RemoteException e) {
			e.printStackTrace();
			return;
		}
		
		try {			
			// Bind the remote object's stub in the registry
			registry = LocateRegistry.getRegistry();
			registry.rebind("RemoteMasterSearch", stub);
		} catch (RemoteException e1) {
			System.err.print("Failed to bind to RMI registry. Will try to start it...");
			try{
				registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
				registry.rebind("RemoteMasterSearch", stub);
			}catch(RemoteException e2){
				System.err.println("FAILED. Program aborted.");
				return;
			}
			System.err.println("SUCCESS.");
		}
		
		System.err
		.println("Master server started. Waiting for the slave to initialize...");
		
		// Finally, launch the master after waiting for the slave to initialize
		try {
			while (MasterSlaveCommunication.getInstance().getSlave() == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			System.err.println("Slave connected, launching JPF...");
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
