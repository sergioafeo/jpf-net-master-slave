package java.io;

public class NetworkLayerInputStream extends InputStream {

	private int socketID;
	
	public NetworkLayerInputStream(int sockID) {
		this.socketID = sockID;
	}
	
	native int native_read(int id);
	
	@Override
	public int read() throws IOException {
		return native_read(socketID);
	}

}
