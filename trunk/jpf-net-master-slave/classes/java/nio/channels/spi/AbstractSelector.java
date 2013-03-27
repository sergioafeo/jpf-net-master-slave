package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class AbstractSelector extends Selector {

	public final void close() throws IOException {
		for (SelectionKey key : registeredKeys) {
			SocketChannel channel = (SocketChannel) key.channel();
			channel.socket().close();
		}
		registeredKeys.clear();
		readyKeys.clear();
		cancelledKeys.clear();
	}
	
	protected AbstractSelector(){
		super();
	}

}
