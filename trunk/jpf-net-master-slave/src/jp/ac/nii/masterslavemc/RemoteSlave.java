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
		Config configSlave = JPF.createConfig(args);

		// Set the configuration parameters
		configSlave.setProperty("search.class",
				"jp.ac.nii.masterslavemc.SlaveSearch");
		configSlave.setProperty(Config.TARGET_KEY,
				"jp.ac.nii.masterslavemc.examples.SimpleSlave");
		// Obtain JPF instance
		JPF slave = new JPF(configSlave);

		MasterSlaveCommunication slaveInstance = new MasterSlaveCommunication();
		MasterSlaveCommunication.setInstance(slaveInstance);
		// Start the communication
		// Get the RMI infrastructure up and running, connect to the master
		try {
			Registry registry = LocateRegistry.getRegistry("localhost");
			IMasterSlaveCommunication stub = (IMasterSlaveCommunication) registry.lookup("RemoteMasterSearch");
			MasterSlaveCommunication.getInstance().setMaster(stub);
			MasterSlaveCommunication.getInstance().getMaster().setSlave((IMasterSlaveCommunication) UnicastRemoteObject.exportObject(slaveInstance,0));
			slave.run();
		} catch (RemoteException e1) {
			e1.printStackTrace();

		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
