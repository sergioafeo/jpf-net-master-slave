package gov.nasa.jpf.jvm;

import gov.nasa.jpf.JPF;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import jp.ac.nii.masterslavemc.Channel;
import jp.ac.nii.masterslavemc.NetworkLayer;
import jp.ac.nii.masterslavemc.Channel.ChannelType;
import jp.ac.nii.masterslavemc.NetworkMessage;

public class JPF_java_net_ServerSocket {
	public static final NetworkLayer net = NetworkLayer.getInstance();
	private static Logger log = JPF.getLogger("gov.nasa.jpf.jvm");

	public static int native_accept_____3I(MJIEnv env, int objRef) throws IOException {
		int port = env.getIntField(objRef, "port");
		Set<NetworkMessage> newSockets = net.accept(port);
		if (newSockets != null && !newSockets.isEmpty()){
		 int newArray = env.newIntArray(newSockets.size());
		 int i = 0;
		 for (NetworkMessage m : newSockets){
			 env.setIntArrayElement(newArray, i++, m.getOrigin().getId());
		 }
		 return newArray;
		} else {
			return env.newIntArray(0);
		}
	}

	public static boolean native_init(MJIEnv env, int objRef, int port) {
		Channel newC =  Channel.get(ChannelType.SERVER, port);
		if (!net.containsKey(newC))
			net.newChannel(ChannelType.SERVER, port);
		else return false;
		return true;
	}
}
