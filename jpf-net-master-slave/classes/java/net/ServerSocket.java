package java.net;

import gov.nasa.jpf.jvm.Verify;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Model class for ServerSocket, exposes part of the functionality of the original
 * class, with native calls that interact with the Master-Slave network layer.
 * 
 * @author Sergio A. Feo
 *
 */
public class ServerSocket implements java.io.Closeable{
	private int port;

	// Constructors
	// For now, only care about ServerSockets with a port and
	// without a special binding address
	public ServerSocket() throws IOException {
    }
	
	private native boolean native_init(int port);
	
	public ServerSocket(int port) throws IOException {
		this.port = port;
		if (!native_init(port))
			throw new IOException("A ServerSocket on port "+port+" already exists.");
	}

	public ServerSocket(int port, int backlog) throws IOException {
        this(port);
    }
	
	public ServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException {
       this(port);
    }
	
	// Methods
	
	public native Socket[] native_accept();
	
	public Socket accept() throws IOException {
		Socket[] sockets = native_accept();
		int index = Verify.getInt(0, sockets.length-1);
		return sockets[index];
	}

	@Override
	public void close() throws IOException {
		// Ignore closing for now
	}
}
