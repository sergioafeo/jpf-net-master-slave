package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;

import java.util.Set;

import jp.ac.nii.masterslavemc.NetworkLayer;
import jp.ac.nii.masterslavemc.NetworkMessage;

public class JPF_java_io_NetworkLayerInputStream extends NativePeer {
	private static NetworkLayer net = NetworkLayer.getInstance();
	
	@MJI
	public static int native_read__I___3I(MJIEnv env, int robj, int sockID){
		Set<NetworkMessage> bytes = net.read(env, sockID);
		if (bytes != null && bytes.size() > 0){
			int result = env.newIntArray(bytes.size());
			ElementInfo ei = env.getElementInfo(result);
			int idx = 0;
			for (NetworkMessage m : bytes){
				ei.setIntElement(idx++, m.getContents());
			}
			return result;
		}
		return env.newIntArray(0);
	}
}
