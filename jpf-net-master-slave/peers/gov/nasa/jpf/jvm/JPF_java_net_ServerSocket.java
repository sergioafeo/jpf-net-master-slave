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

	public static int native_accept_____3Ljava_net_Socket_2(MJIEnv env, int objRef) throws IOException {
		int port = env.getIntField(objRef, "port");
		Set<NetworkMessage> newSockets = net.accept(port);
		if (newSockets != null && !newSockets.isEmpty()){
		 int newArray = env.newObjectArray("java.net.Socket", newSockets.size());
		 ElementInfo ei = env.getElementInfo(newArray);
		 int i = 0;
		 for (NetworkMessage m : newSockets){
			 int newSocket = env.newObject("java.net.Socket");
			 env.setIntField(newSocket, "socketID", m.getOrigin().getId());
			 ei.setElementAttr(i++, newSocket);
		 }
		 return newArray;
		} else env.getVM().ignoreState();
		return -1;
	}

	public static boolean native_init(MJIEnv env, int objRef, int port) {
		Channel newC =  new Channel(ChannelType.SERVER, port);
		if (!net.containsKey(newC))
			net.newChannel(ChannelType.SERVER, port);
		else return false;
		return true;
	}
}
