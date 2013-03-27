package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Set;

/** 
 * An ineffective but basic implementation of the selector
 * @author amarda
 *
 */
public abstract class Selector {
	protected Set <SelectionKey> registeredKeys;
	protected Set <SelectionKey> cancelledKeys;
	protected Set <SelectionKey> readyKeys;
	private boolean init = false;

	protected Selector() {
		registeredKeys = new HashSet<SelectionKey>();
		cancelledKeys = new HashSet<SelectionKey>();
		readyKeys = new HashSet<SelectionKey>();
	}

	public int select() throws IOException {
		if (!init) {
			init = initSelector();
		}
		while (readyKeys.size() <= 0) {
			removeCancelledKeys();
			checkReadiness();
			removeCancelledKeys();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		return readyKeys.size();
	}

	private boolean initSelector() {
		//TODO only support program that uses nio for Server application
		for (SelectionKey key : registeredKeys) {
			if (key.channel() instanceof ServerSocketChannel) {
				ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
				if (!serverChannel.isBound) {
					serverChannel.bindServer();
				}
			}
		}
		return true;
	}

	public static Selector open() throws IOException {
		return SelectorProvider.provider().openSelector();
	}

	public Set<SelectionKey> selectedKeys() {
		return readyKeys;
	}

	public Set<SelectionKey> keys(){
		return registeredKeys;
	}

	public abstract void close() throws IOException;

	protected void cancelKey(SelectionKey key){
		if (registeredKeys.contains(key)) {
			synchronized (cancelledKeys) {				
				cancelledKeys.add(key);
			}
		} else {
			System.err.println("Trying to cancel an unknown key");
		}
	}

	private synchronized void removeCancelledKeys(){
		for (SelectionKey key : cancelledKeys) {
			System.out.println("A key has been removed");
			readyKeys.remove(key);
			registeredKeys.remove(key);
		}
		cancelledKeys.clear();
	}

	private void checkReadiness() throws IOException{
		for (SelectionKey key : registeredKeys) {
			key.readyOps(0); // we reset the key ready ops
			key = checkAcceptable(key);
			key = checkReadable(key);
			key = checkWritable(key);
			if (key.readyOps() != 0) {
				readyKeys.add(key);
			}
		}
	}

	private SelectionKey checkAcceptable(SelectionKey key) {
		if ((key.interestOps() & SelectionKey.OP_ACCEPT) != 0) {
			ServerSocketChannel channel = (ServerSocketChannel) key.channel();
			if ((channel.isBound) && (channel.hasPendingConnection())) {
				System.out.println("cool");
				key.readyOps(key.readyOps()|SelectionKey.OP_ACCEPT);
			} 
		}
		return key;
	}

	private SelectionKey checkReadable(SelectionKey key) {
		if ((key.interestOps() & SelectionKey.OP_READ) != 0) {
			SocketChannel channel = (SocketChannel) key.channel();
			if (channel.isReadable()) {
				key.readyOps(key.readyOps()|SelectionKey.OP_READ);
			} 
		}
		return key;
	}

	private SelectionKey checkWritable(SelectionKey key) {
		if ((key.interestOps() & SelectionKey.OP_WRITE) != 0) {
			//TODO must check the send buffer
			key.readyOps(key.readyOps()|SelectionKey.OP_WRITE);
		} 
		return key;
	}

	protected SelectionKey registerKey(SelectableChannel channel){
		SelectionKey key = new SelectionKey();
		key.setChannel(channel);
		key.setSelector(this);
		registeredKeys.add(key);
		return key;
	}
}
