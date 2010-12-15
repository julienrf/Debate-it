package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.Play;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.Codec;
import utils.Helper;

@Entity
public class Room extends Model {

	@Required
	public String name;
	
	@Required
	public Boolean isPublic;
	
	public String hash;
	
	@OneToMany(mappedBy="room", cascade=CascadeType.REMOVE)
	public List<Thread> threads;
	
	protected Room(String name, boolean isPublic) {
		this.name = name;
		this.isPublic = isPublic;
		this.threads = new ArrayList<Thread>();
	}

	public static Room create(User creator, String name, boolean isPublic) {
		Room room = new Room(name, isPublic);
		room.save();
		room.hash = Helper.hexTo62(Codec.hexMD5(Play.secretKey + room.id));
		room.save();
		creator.subscribe(room); // TODO handle administration rights
		return room;
	}
	
	public static Room getOpenRoom() {
		Room room = Room.find("byHash", "open").first();
		if (room == null) { // Create it if it does not exist
			room = new Room("Open Room", true);
			room.hash = "open";
			room.save();
		}
		return room;
	}
	
	public Thread lastActivity() {
		if (threads.size() == 0)
			return null;
		else
			return Thread.sortByLastPost(threads).get(0); // TODO utiliser autre chose que Siena, parce que ça me gave de ne pas avoir de jointures
	}
}
