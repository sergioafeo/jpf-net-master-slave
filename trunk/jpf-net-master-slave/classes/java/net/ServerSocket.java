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

package java.net;

import java.io.IOException;

import jp.ac.nii.masterslavemc.Channel;
import jp.ac.nii.masterslavemc.NetworkLayer;
import jp.ac.nii.masterslavemc.Channel.ChannelType;

public class ServerSocket {
	private static NetworkLayer net = NetworkLayer.getInstance();
	int port = -1;

	public ServerSocket() {
		// unbound instance
	}
	
	public ServerSocket(int port) throws IOException {
		this.port = port;
		Channel newSSocket = new Channel(ChannelType.SERVER, port); 
		if (net.containsKey(newSSocket))
			throw new IOException();
		else
		 net.newChannel(ChannelType.SERVER, port);
	}
	
	public ServerSocket(int port, int backlog) throws IOException {
		// Ignore backlog.
		this(port);
	}
	
	public ServerSocket(int port, int backlog, InetAddress bindAddr)
			throws IOException {
		this(port);
	}

	public native void setSoTimeout(int timeout) throws SocketException;
	
	public native int getSoTimeout() throws IOException;
	
	private native int native_accept() throws IOException;
	
	public native boolean isClosed();

	public void bind(SocketAddress address) {
		port = ((InetSocketAddress) address).getPort();
	}
	
	public Socket accept() throws IOException {
		Socket s = new Socket();
		native_accept();
		return s;
	}

	public Socket acceptNio() throws IOException {
		Socket s = new Socket();
		int id = native_accept();
		return s;
	}

	public void close() throws IOException {
	}

	public int getLocalPort() {
		return port;
	}
	
	public void setReuseAddress(boolean on) {
		// empty stub
		// TODO: Model TIME_WAIT state in TCP/IP
	}
}
