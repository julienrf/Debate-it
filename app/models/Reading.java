package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * A reading is a triplet linking a user and a thread with the date of the last post the user has read in the thread
 * @author julien
 *
 */
@Entity
public class Reading extends Model {

	@Required
	@ManyToOne
	public Thread thread;
	
	@Required
	@ManyToOne
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
	
	public String toString() {
		return date.toString();
	}
	
	public void updateDate(Date date) {
		if (this.date.compareTo(date) < 0) {
			this.date = date;
			save();
		}
	}
}
