package models;

import java.util.Date;

import play.data.validation.Required;
import siena.Id;
import siena.Index;
import siena.Model;
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
	@Index("thread_index")
	public Thread thread;
	
	@Required
	@Index("user_index")
	public User user;
	
	@Required
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
