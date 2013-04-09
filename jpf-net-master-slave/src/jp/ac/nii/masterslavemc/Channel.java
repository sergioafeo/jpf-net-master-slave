package jp.ac.nii.masterslavemc;

import java.io.Serializable;
import java.util.HashMap;

import jp.ac.nii.masterslavemc.Channel.ChannelType;

class ChannelID {
	public ChannelID(ChannelType type, int id) {
		this.type = type;
		this.id = id;
	}

	ChannelType type;
	int id;
	
	@Override
	public boolean equals(Object x){
		if (x instanceof ChannelID)
			return ((ChannelID)x).type == type && ((ChannelID)x).id==id;
		return false;
	}

	@Override
	public int hashCode() {
		return type.ordinal() * 127 + id * 3571;
	}
}

public class Channel implements Serializable{

	private static HashMap<ChannelID, Channel> instances = new HashMap<ChannelID, Channel>();
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(type);
		sb.append(":");
		sb.append(id);
		return sb.toString();
	}

	private static final long serialVersionUID = 6773095734481426847L;
	
	public enum ChannelType{
		SERVER,
		CLIENT
	}
	
	private ChannelType type;
	private int id;

	private Channel(ChannelType socketType, int socketID) {
		id= socketID;
		type = socketType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

	public ChannelType getType() {
		return type;
	}

	public void setType(ChannelType type) {
		this.type = type;
	}

	// Implement the flyweight pattern
	public static synchronized Channel get(ChannelType type2, int id2) {
		ChannelID tmp = new ChannelID(type2, id2);
		Channel result = instances.get(tmp);
		if (result != null)
			return result;
		result = new Channel(type2,id2);
		instances.put(tmp, result);
		return result;
	}
}
