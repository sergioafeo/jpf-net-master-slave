package java.io;

public class NetworkLayerOutputStream extends OutputStream {
	
	private int socketID;
	public NetworkLayerOutputStream(int sockID) {
		this.socketID = sockID;
	}
	
	native void native_write(int sockID, int b);
	
	@Override
	public void write(int b) throws IOException {
		native_write(socketID, b);
	}
}
