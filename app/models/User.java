package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {
	
	/** Display name */
	@Required
	public String name;
	
	/** Email identifying the user */
	@Required
	@Email
	public String email;
	
	/** User TimeZone */
	@Required
	public TimeZone tz;
	
	/** Remember the user in a cookie */
	public boolean rememberMe;
	
	/** Set of read thread */
	/*@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	public List<Reading> readings;*/
	
	public User(String name, String email, TimeZone tz, boolean rememberMe) {
		this.name = name;
		this.email = email;
		this.tz = tz;
		this.rememberMe = rememberMe;
		//this.readings = new ArrayList<Reading>();
	}
	
	/**
	 * Update user attributes
	 */
	public void update(Update update) {
		name = update.name;
		tz = TimeZone.getTimeZone(update.tzId);
		rememberMe = update.rememberMe;
		save();
	}
	
	/**
	 * Set a thread read
	 * @param thread
	 */
	/*public void setThreadRead(Thread thread) {
		Reading reading = Reading.find("byThreadAndUser", thread, this).first();
		if (reading != null) {
			reading.date = new Date();
		} else {
			reading = new Reading(thread, this, new Date());
		}
		reading.save();
	}*/
	
	/**
	 * Retrieve the date of the last post read in a given thread
	 * @param thread
	 * @return
	 */
	public Reading lastReading(Thread thread) {
		Reading reading = Reading.find("byThreadAndUser", thread, this).first();
		if (reading == null) // Creates the reading if it doesn't exist
			reading = new Reading(thread, this, new Date()).save();
		return reading;
	}
	
	public String toString() {
		return name;
	}

	public static class Update {
		@Required public String name;
		@Required public String tzId;
		@Required public boolean rememberMe;
		
		public Update(User user) {
			name = user.name;
			tzId = user.tz.getID();
			rememberMe = user.rememberMe;
		}
	}
}
