//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

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

		if (!isInitialized)
			init(env);

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
