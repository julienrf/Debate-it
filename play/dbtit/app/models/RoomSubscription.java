package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

// FIXME Use a @ManyToMany relation instead of using this class
@Entity
public class RoomSubscription extends Model {

	@Required
	@ManyToOne
	public User user;
	
	@Required
	@ManyToOne
	public Room room;
	
	public RoomSubscription(Room room, User user) {
		this.room = room;
		this.user = user;
	}
}
