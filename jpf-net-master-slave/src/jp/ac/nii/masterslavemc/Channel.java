package jp.ac.nii.masterslavemc;

import java.io.Serializable;

public class Channel implements Serializable{
	private static final long serialVersionUID = 6773095734481426847L;
	
	public enum ChannelType{
		SERVER,
		CLIENT
	}
	
	private ChannelType type;
	private int id;

	public Channel(ChannelType socketType, int socketID) {
		id= socketID;
		type = socketType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean equals(Object x){
		if (x instanceof Channel)
			return ((Channel)x).type == type && ((Channel)x).id==id;
		return false;
	}

	public ChannelType getType() {
		return type;
	}

	public void setType(ChannelType type) {
		this.type = type;
	}
}
