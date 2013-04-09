package jp.ac.nii.masterslavemc;

import java.io.Serializable;

/**
 * Bundles the payload of a network message and the VM state of the program which sent it.
 * 
 * @author Sergio A. Feo
 */
public class NetworkMessage implements Serializable{

	private static final long serialVersionUID = -4055812705206158686L;

	private Channel origin;
	public Channel getOrigin() {
		return origin;
	}
	public void setOrigin(Channel origin) {
		this.origin = origin;
	}
	/**
	 * Payload of the network message
	 */
	private int contents;
	private boolean connect;
	
	public boolean isConnect() {
		return connect;
	}
	public void setConnect(boolean connect) {
		this.connect = connect;
	}
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
	public int getState() {
		return state;
	}
	public void setContents(byte contents) {
		this.contents = contents;
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
