package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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

	@OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE)
	@OrderBy("lastActivity DESC")
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
	
	public static List<RoomThread> getRecentPublicActivity(int from, int max) {
		Query q = Room.em().createQuery("SELECT DISTINCT r, t FROM Room r, Thread t WHERE t.room = r AND r.isPublic = true ORDER BY t.lastActivity DESC)");
		q.setFirstResult(from);
		q.setMaxResults(max);
		List<RoomThread> rooms = new ArrayList<RoomThread>(); // Mapping à la main :( Je déteste JPA et Java.
		for (Object[] o : (List<Object[]>)q.getResultList()) {
			rooms.add(new RoomThread((Room)o[0], (Thread)o[1]));
		}
		return rooms;
	}
	
	public static class RoomThread {
		public Room room;
		public Thread thread;
		
		public RoomThread(Room room, Thread thread) {
			this.room = room;
			this.thread = thread;
		}
	}
}
