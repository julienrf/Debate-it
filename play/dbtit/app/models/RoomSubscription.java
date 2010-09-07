package models;

import siena.Column;
import siena.Id;
import siena.Model;
import siena.NotNull;
import siena.Query;

public class RoomSubscription extends Model {

	@Id
	public Long id;
	
	@NotNull @Column("user")
	public User user;
	
	@NotNull @Column("room")
	public Room room;
	
	public RoomSubscription(Room room, User user) {
		this.room = room;
		this.user = user;
	}
	
	public static Query<RoomSubscription> all() {
		return Model.all(RoomSubscription.class);
	}
}
