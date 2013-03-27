package java.nio.channels;

import java.io.IOException;

public abstract class SelectableChannel {
	public abstract void close() throws IOException;
	public abstract SelectableChannel configureBlocking(boolean block);
	public abstract int validOps();
}
