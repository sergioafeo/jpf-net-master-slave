package gov.nasa.jpf.jvm;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import java.io.IOException;
import java.util.logging.Logger;

import jp.ac.nii.masterslavemc.Channel;
import jp.ac.nii.masterslavemc.NetworkLayer;
import jp.ac.nii.masterslavemc.Channel.ChannelType;

//import gov.nasa.jpf.network.SystemInfo;
//import gov.nasa.jpf.network.cache.CacheLayer;

public class JPF_java_net_ServerSocket {
	public static final NetworkLayer net = NetworkLayer.getInstance();
	private static Logger log = JPF.getLogger("gov.nasa.jpf.jvm");

	public static int native_accept(MJIEnv env, int objRef) throws IOException {
		int port = env.getIntField(objRef, "port");
		int newSocket = -1;
		if (net.accept(port)){
		 newSocket = env.newObject("java.net.Socket");
		}
		return newSocket;
	}

	public static boolean native_init(MJIEnv env, int objRef, int port) {
		Channel newC =  new Channel(ChannelType.SERVER, port);
		if (!net.containsKey(newC))
			net.newChannel(ChannelType.SERVER, port);
		else return false;
		return true;
	}
}
