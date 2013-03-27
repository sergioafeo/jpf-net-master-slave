package java.nio.channels;

public class SelectionKey {

	public static final int OP_ACCEPT = 16;
	public static final int OP_CONNECT = 2;
	public static final int OP_READ = 1;
	public static final int OP_WRITE = 4;

	private SelectableChannel channel;
	private Selector selector;
	
	private int readyOps;
	private int interestOps;
	private boolean valid;
	
	private Object attachment;

	protected SelectionKey(){
		attachment = null;
		valid = true;
		readyOps = 0;
		interestOps = 0;
	}


	public SelectableChannel channel() {
		return channel;
	}
	
	public Selector selector(){
		return selector;
	}

	public boolean isValid() {
		return this.valid;
	}
	
	public void cancel() {
		if (valid) {
			valid = false;
			selector.cancelKey(this);
		}
	}

	public int interestOps() throws CancelledKeyException {
		if (!isValid()) {
			throw new CancelledKeyException();
		}
		return this.interestOps;
	}
	
	public SelectionKey interestOps(int i) throws IllegalArgumentException, CancelledKeyException {
		if (((i&~(channel.validOps())) != (0))) {
			throw new IllegalArgumentException("Impossible to change interestOps : Illegal channel interestOps");
		} else if (!isValid()) {
			throw new CancelledKeyException();
		}
		this.interestOps = i;
		return this;
	}

	
	public int readyOps() throws CancelledKeyException {
		if (!isValid()) {
			throw new CancelledKeyException();
		}
		return this.readyOps;
	}

	public boolean isReadable() throws CancelledKeyException{
		if (!isValid()) {
			throw new CancelledKeyException();
		}
		return (this.readyOps & OP_READ) != 0;
	}
	
	public boolean isWritable() throws CancelledKeyException{
		if (!isValid()) {
			throw new CancelledKeyException();
		}
		return (this.readyOps & OP_WRITE) != 0;
	}
	
	public boolean isConnectable() throws CancelledKeyException{
		if (!isValid()) {
			throw new CancelledKeyException();
		}
		return false;
	}
	
	public boolean isAcceptable() throws CancelledKeyException{
		if (!isValid()) {
			throw new CancelledKeyException();
		}
		return (this.readyOps & OP_ACCEPT) != 0;
	}
	
	public Object attach(Object attachment) {
		return this.attachment = attachment;
	}
	
	public Object attachment() {
		return attachment;
	}

	protected void setSelector(Selector selector){
		this.selector = selector;
	}

	protected void setChannel(SelectableChannel channel) {
		this.channel = channel;
	}

	protected void readyOps(int readyOps) {
		this.readyOps = readyOps;
	}
	
	public boolean equals(Object o) {
		if (o instanceof SelectionKey) {
			if ((((SelectionKey) o).channel == this.channel) &&
					(((SelectionKey) o).selector == this.selector)) {
				return true;
			} else {
				return false;
			}
			
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "acceptable: "+isAcceptable()+" readable: "+isReadable()+" writable: "+isWritable();
	}
}
