package java.nio.channels;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class ServerSocketChannel extends SelectableChannel {
	private Object lock = new Object();

	private class AcceptorThread extends Thread {
		boolean notClosed = false;	
		public void run() {
			Socket socket;
			while (!notClosed) {
				try {
					synchronized (lock) {
						while ((acceptableSockets.size()!=0) ||  (key.interestOps() & SelectionKey.OP_ACCEPT) == 0 ) {
							try {
								lock.wait(100);
							} catch (InterruptedException e) {}
						}
					}
					socket = serverSocket.accept();
					synchronized (acceptableSockets) {
						acceptableSockets.add(socket);
					}
				} catch (IOException e) {
					System.err.println("A client failed to connect: ");
					e.printStackTrace();
				}
			}
		}
	}

	ServerSocket serverSocket;
	LinkedList<Socket> acceptableSockets;
	AcceptorThread acceptorThread;
	boolean isBound = false;
	SelectionKey key;
	
	private ServerSocketChannel() throws IOException {		
		serverSocket = new ServerSocket();
		acceptableSockets = new LinkedList<Socket>();
	}


	public SocketChannel accept() throws IOException {
		synchronized (lock) {
			assert !acceptableSockets.isEmpty() : "trying to accept an unready connection";
			return new SocketChannel(acceptableSockets.removeFirst());
		}
	}

	public static ServerSocketChannel open() throws IOException {
		return new ServerSocketChannel();
	}

	public ServerSocket socket() {
		return this.serverSocket;
	}

	protected boolean bindServer() {
		acceptorThread = new AcceptorThread();
		acceptorThread.start();
		return isBound = true;
	}

	protected boolean hasPendingConnection() {
		synchronized (lock) {
			lock.notifyAll();
			return acceptableSockets.size()>0;			
		}
	}


	public SelectableChannel configureBlocking(boolean b) {
		// TODO not implemented yet
		return null;
	}

	public SelectionKey register(Selector selector, int opAccept) {
		SelectionKey key = selector.registerKey(this);
		key.interestOps(opAccept);
		this.key = key;
		return key;

	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public final int validOps() {
		return 16;
	}	
}
