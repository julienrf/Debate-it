package models;

import java.util.Date;

import play.data.validation.Required;
import siena.Column;
import siena.Id;
import siena.Model;
import siena.NotNull;
import siena.Query;

/**
 * A reading is a triplet linking a user and a thread with the date of the last post the user has read in the thread
 * @author julien
 *
 */
public class Reading extends Model {
	
	@Id
	public Long id;

	@Required
	@NotNull @Column("thread")
	public Thread thread;
	
	@Required
	@NotNull @Column("user")
	public User user;
	
	@Required
	@NotNull
	public Date date;
	
	public Reading(Thread thread, User user, Date date)
	{
		this.thread = thread;
		this.user = user;
		this.date = date;
		//this.user.readings.add(this); // maintains the relationship
		//this.thread.readings.add(this); // maintains the relationship
	}
	
	public static Query<Reading> all() {
		return Model.all(Reading.class);
	}
	
	public String toString() {
		return date.toString();
	}
	
	public void updateDate(Date date) {
		if (this.date.compareTo(date) < 0) {
			this.date = date;
			update();
		}
	}
}
