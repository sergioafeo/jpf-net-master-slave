package gov.nasa.jpf.jvm;

import jp.ac.nii.masterslavemc.NetworkLayer;

public class JPF_java_io_NetworkLayerOutputStream {
	private static NetworkLayer net = NetworkLayer.getInstance();
	public static void native_write__II__V (MJIEnv env, int objRef, int sockID, int b) {
		net.write(env,sockID, b);
	  }
}
