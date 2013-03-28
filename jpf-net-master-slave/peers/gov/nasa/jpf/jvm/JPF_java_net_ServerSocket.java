package gov.nasa.jpf.jvm;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import java.io.IOException;
import java.util.logging.Logger;

//import gov.nasa.jpf.network.SystemInfo;
//import gov.nasa.jpf.network.cache.CacheLayer;

public class JPF_java_net_ServerSocket {

	private static Logger log = JPF.getLogger("gov.nasa.jpf.jvm");

	private static boolean isInitialized = false;

	public static int native_accept____I(MJIEnv env, int objRef) throws IOException {
//		CacheLayer cache = CacheLayer.getInstance();
		int port = env.getIntField(objRef, "port");
		
//		return cache.accept(port);
		return 0;
	}

	public static boolean isClosed(MJIEnv env, int objRef) {
//		return CacheLayer.getInstance().isServerSocketClosed();
		return false;
	}

	private static void init(MJIEnv env) {
		Config cfg = env.getConfig();
//		String boot_peer_cmd = cfg.getString(SystemInfo.BOOT_CMD);
		String arg = "";

//		log.info(String.format("[SERVER_SOCKET_PEER : accept()] %s=%s", SystemInfo.BOOT_CMD, boot_peer_cmd));

//		CacheLayer.addBootPeerCommand(boot_peer_cmd);

//		for (int i = 0; arg != null; i++) {
//			arg = cfg.getString(SystemInfo.CMD_ARG + i);

//			if (arg != null)
//				CacheLayer.addBootPeerCommand(arg);

//			log.info(String.format("[SERVER_SOCKET_PEER : accept()] %s%d=%s", SystemInfo.CMD_ARG, i, arg));
//		}

		isInitialized = true;
	}
}
