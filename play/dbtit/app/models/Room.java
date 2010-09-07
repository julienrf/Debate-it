package models;

import play.Play;
import play.data.validation.Required;
import play.libs.Codec;
import siena.Filter;
import siena.Id;
import siena.Model;
import siena.NotNull;
import siena.Query;
import utils.Helper;

public class Room extends Model {

	@Id
	public Long id;
	
	@Required
	@NotNull
	public String name;
	
	/*@Required
	@NotNull
	public Boolean isPublic;*/
	
	public String hash;
	
	@Filter("room")
	public Query<Thread> threads;
	
	protected Room(String name/*, boolean isPublic*/) {
		this.name = name;
		/*this.isPublic = isPublic;*/
	}

	public static Query<Room> all() {
		return Model.all(Room.class);
	}
	
	public static Room findByHash(String hash) {
		return Room.all().filter("hash", hash).get();
	}
	
	public static Room create(User creator, String name/*, boolean isPublic*/) {
		Room room = new Room(name/*, isPublic*/);
		room.insert();
		room.hash = Helper.hexTo62(Codec.hexMD5(Play.secretKey + room.id));
		room.update();
		creator.subscribe(room); // TODO handle administration rights
		return room;
	}
	
	public static Room getOpenRoom() {
		Room room = Room.findByHash("open");
		if (room == null) { // Create it if it does not exist
			room = new Room("Open Room"/*, true*/);
			room.hash = "open";
			room.insert();
		}
		return room;
	}
}
