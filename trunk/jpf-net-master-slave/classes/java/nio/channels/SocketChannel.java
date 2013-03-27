package java.nio.channels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SocketChannel extends SelectableChannel {
	
	private Socket socket;
	private SelectionKey key;
	private InputStream in;
	private OutputStream out;
	private ByteBuffer buffer;
	static final int DEFAULT_SIZE = 4096;
	
	/**
	 * For
	 * @param socket
	 * @throws IOException 
	 */
	protected SocketChannel(Socket socket) throws IOException {
		this.socket = socket;
		buffer = ByteBuffer.allocate(DEFAULT_SIZE);
		in = socket.getInputStream();
		out = socket.getOutputStream();
	}

	public SelectableChannel configureBlocking(boolean b) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public SelectionKey register(Selector selector, int opRead) {
		SelectionKey key = selector.registerKey(this);
		key.interestOps(opRead);
		return key;
	}

	public int read(ByteBuffer readBuffer) throws IOException {
		int count = 0;
		while (isReadable()&&(readBuffer.hasRemaining())) {
			readBuffer.put((byte) in.read());
			count++;
		}
		return count;
	}

	public int write(ByteBuffer buffer) throws IOException {
		int count = 0;
		while (buffer.hasRemaining()) {
			out.write(buffer.get());
			count ++;
		}
		return count;
	}

	public Socket socket(){
		return socket;
	}

	@Override
	public void close() throws IOException {
		socket.close();
		key.cancel();
	}

	@Override
	public final int validOps() {
		// TODO magic number
		return 5;
	}
	
	protected boolean isReadable() {
		try {
			if (!socket.isConnected()) {
				return true;
			}
			return in.available()!=0;
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		} 
	}
}
