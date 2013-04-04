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

import java.io.IOException;
import java.net.InetAddress;

import jp.ac.nii.masterslavemc.NetworkLayer;
import jp.ac.nii.masterslavemc.Channel.ChannelType;

import gov.nasa.jpf.jvm.MJIEnv;

public class JPF_java_net_Socket {

	public static void native_Socket____(MJIEnv env, int objRef) {
		int id;
		id = env.getIntField(objRef, "socketID");
		NetworkLayer.getInstance().newChannel(ChannelType.CLIENT, id);
	}

	public static void native_connect__Ljava_net_InetAddress_2I(MJIEnv env, int objRef, int addrRef, int port)
			throws IOException {

		int id;
		id = env.getIntField(objRef, "socketID");
		NetworkLayer.getInstance().newChannel(ChannelType.CLIENT, id);
		
		InetAddress addr;
		byte[] ip;
		int address_ref;

		// Virtualized mode: The cache must connect to a real IP address.
		// If the address is not specified, use localhost.
//		if (CacheLayer.isVirtualMode() && addrRef > 0) {
			address_ref = env.getReferenceField(addrRef, "address");
			ip = env.getByteArrayObject(address_ref);
			addr = InetAddress.getByAddress(ip);
//		} else
//			addr = InetAddress.getLocalHost();

		int sock_id = env.getIntField(objRef, "socketID");

//		cl = CacheLayer.getInstance();
//		cl.connect(sock_id, addr, port);
	}

	public static boolean isMainAlive(MJIEnv env, int objRef) {
		return false;		
//		return CacheLayer.getInstance().isMainAlive();
	}

	public static int native_getInetAddress(MJIEnv env, int objRef) {
//		PhysicalConnection c;
		byte[] addr;
		int ipRef = env.newByteArray(4);
		int sock_id = env.getIntField(objRef, "socketID");

//		c = CacheLayer.conn_list.get(sock_id);
//		addr = c.getInetAddress().getAddress();

//		for (int i = 0; i < 4; i++)
//			env.setByteArrayElement(ipRef, i, addr[i]);

		return ipRef;
	}

	public static int getPort(MJIEnv env, int objRef) {
//		PhysicalConnection c;
		int sock_id = env.getIntField(objRef, "socketID");

//		c = CacheLayer.conn_list.get(sock_id);
//		return c.getPort();
		return 0;
	}

	public static int getLocalPort(MJIEnv env, int objRef) {
//		PhysicalConnection c;
		int sock_id = env.getIntField(objRef, "socketID");
		return 0;

//		c = CacheLayer.conn_list.get(sock_id);
//		return c.getLocalPort();
	}

	public static boolean isConnected(MJIEnv env, int objRef) {
//		PhysicalConnection c;
		int sock_id = env.getIntField(objRef, "socketID");

//		c = CacheLayer.conn_list.get(sock_id);
//		return c.isConnected();
		return false;
	}

	public static boolean isClosed(MJIEnv env, int objRef) {
//		CacheLayer cl = CacheLayer.getInstance();
		int sock_id = env.getIntField(objRef, "socketID");
		return false;

//		return cl.isSocketClosed(sock_id);
	}

	public static void close(MJIEnv env, int objRef) throws IOException {
		int sock_id = env.getIntField(objRef, "socketID");

//		CacheLayer.getInstance().close(sock_id);
	}
}
