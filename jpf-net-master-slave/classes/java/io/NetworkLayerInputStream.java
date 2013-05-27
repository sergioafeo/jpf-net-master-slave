package java.io;

import gov.nasa.jpf.vm.Verify;

public class NetworkLayerInputStream extends InputStream {

	private int socketID;
	
	public NetworkLayerInputStream(int sockID) {
		this.socketID = sockID;
	}
	
	native int[] native_read(int id);
	
	@Override
	public int read() throws IOException {
		int[] read_bytes = native_read(socketID);
		if (read_bytes == null) throw new IOException("Read non-existent or closed socket.");
		if (read_bytes.length > 0) {
			return read_bytes[Verify.getInt(0, read_bytes.length-1)];
		}
		else 
		return -1;
	}

}
