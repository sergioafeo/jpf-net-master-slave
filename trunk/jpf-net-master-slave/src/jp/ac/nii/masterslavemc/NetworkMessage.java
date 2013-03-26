package jp.ac.nii.masterslavemc;

import java.io.Serializable;

import gov.nasa.jpf.State;

/**
 * @author Sergio A. Feo
 * 
 * Bundles the payload of a network message and the VM state of the program which sent it.
 *
 */
public class NetworkMessage implements Serializable{

	private static final long serialVersionUID = -4055812705206158686L;

	/**
	 * Payload of the network message
	 */
	private byte[] contents;
	
	/**
	 * VM State of the slave after sending the message
	 */
	private State state;
	public NetworkMessage(byte[] contents, State state) {
		super();
		this.contents = contents;
		this.state = state;
	}
	public byte[] getContents() {
		return contents;
	}
	public State getState() {
		return state;
	}
	public void setContents(byte[] contents) {
		this.contents = contents;
	}
	public void setState(State state) {
		this.state = state;
	}
}
