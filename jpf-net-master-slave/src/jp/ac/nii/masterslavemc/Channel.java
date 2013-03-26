package jp.ac.nii.masterslavemc;

import java.io.Serializable;

public class Channel implements Serializable{
	private static final long serialVersionUID = 6773095734481426847L;
	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
