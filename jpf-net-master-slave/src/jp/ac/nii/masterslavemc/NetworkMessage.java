package jp.ac.nii.masterslavemc;

import java.io.Serializable;

/**
 * Bundles the payload of a network message and the VM state of the program which sent it.
 * 
 * @author Sergio A. Feo
 */
public class NetworkMessage implements Serializable{

	private static final long serialVersionUID = -4055812705206158686L;

	private boolean connect;
	/**
	 * Payload of the network message
	 */
	private int contents;
	/**
	 * Depth in the search when this message was generated.
	 * -1 signals an uninitialized message.
	 */
	private int depth = -1;
	private Channel origin;
	/**
	 * VM State of the slave after sending the message
	 */
	private int state;
	
	public NetworkMessage(int contents, boolean connect, Channel origin, int state) {
		super();
		this.connect = connect;
		this.contents = contents;
		this.state = state;
		this.origin = origin;
	}
	
	public int getContents() {
		return contents;
	}
	public int getDepth() {
		return depth;
	}
	public Channel getOrigin() {
		return Channel.get(origin.getType(), origin.getId());
	}
	public int getState() {
		return state;
	}
	public boolean isConnect() {
		return connect;
	}
	public void setConnect(boolean connect) {
		this.connect = connect;
	}
	public void setContents(byte contents) {
		this.contents = contents;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public void setOrigin(Channel origin) {
		this.origin = origin;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String toString(){
		if(connect)
			return "["+origin+":CONNECT]@"+state;
		return "["+origin+":"+contents+"]@"+state;
	}
}
