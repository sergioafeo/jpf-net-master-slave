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

import gov.nasa.jpf.jvm.Verify;

import java.io.*;


public class Socket {

	static int numSocket = 0;
	
	int socketID;

	static boolean exception_simulation;

	static boolean main_termination;

//	CacheLayerInputStream in;

	static {
		Object lock = new Object();

//		CacheLayerInputStream.setLock(lock);
	}

	public Socket() {
		socketID = numSocket++;
		native_Socket();
	}

	public Socket(String host, int port) {
		this();
		try {
			native_connect(InetAddress.getByName(host), port);
		} catch (UnknownHostException e) {
			System.err.println("[Socket : Socket(String, int)] " + e);
		}
	}

	public Socket(InetAddress addr, int port) throws IOException {
		this();
		native_connect(addr, port);
	}

	public Socket(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException {
		this(address, port);
	}

	private native void native_Socket();

	private native void native_connect(InetAddress addr, int port);

	public static native boolean isMainAlive();

	public native byte[] native_getInetAddress();

	public native int getPort();

	public native boolean isConnected();

	public native void close() throws IOException;

	public native boolean isClosed();

	public int getLocalPort() {
		return getPort(); // just return something for now
	}

	public InputStream getInputStream() throws IOException {
//		CacheLayerInputStream in = new CacheLayerInputStream(socketID);

//		in.setSimulatedException(exception_simulation);
//		in.setMain_termination(main_termination);
//		this.in = in;
		return new InputStream() {
			
			@Override
			public int read() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

	public OutputStream getOutputStream() throws IOException {
//		CacheLayerOutputStream out = new CacheLayerOutputStream(socketID);
//
//		out.setSimulatedException(exception_simulation);
//		out.setMain_termination(main_termination);
		return new OutputStream() {
			
			@Override
			public void write(int arg0) throws IOException {
				// TODO Auto-generated method stub
				
			}
		};
	}

	public void connect(SocketAddress endpoint) throws IOException {
		// -- properties dependent --
		if (exception_simulation) {
			boolean exception = Verify.getBoolean();

			if (exception)
				throw new IOException("[Simulated] an error occurs during the connection");
		}
		// -- properties dependent --

		if (endpoint instanceof InetSocketAddress) {
			InetSocketAddress inet = (InetSocketAddress) endpoint;

			native_connect(inet.getAddress(), inet.getPort());
		} else
			System.err.println("The specified socket address is not an instance of InetSocketAddress.");
	}

	public InetAddress getInetAddress() {
		try {
			return InetAddress.getByAddress(native_getInetAddress());
		} catch (UnknownHostException e) {
			System.err.println("[Socket : getInetAddress()] " + e);
			System.exit(1);
			return null;
		}
	}

	public void setSoTimeout(int timeout) throws SocketException {
		// stub
	}

	public void setTcpNoDelay(boolean on) throws SocketException {
		// stub
	}

	public void setSoLinger(boolean on, int linger) throws SocketException {
		// stub
	}

	public boolean isChannelReadable() throws IOException {
//		return in.isChannelReadable();
		return false;
	}
}
