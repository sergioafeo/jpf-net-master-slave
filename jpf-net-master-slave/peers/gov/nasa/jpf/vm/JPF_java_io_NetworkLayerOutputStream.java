package gov.nasa.jpf.vm;

import gov.nasa.jpf.annotation.MJI;
import jp.ac.nii.masterslavemc.NetworkLayer;

public class JPF_java_io_NetworkLayerOutputStream extends NativePeer {
	private static NetworkLayer net = NetworkLayer.getInstance();
	
	@MJI
	public static void native_write__II__V (MJIEnv env, int objRef, int sockID, int b) {
		net.write(env,sockID, b);
	  }
}
