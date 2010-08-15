package models;

import java.util.Date;
import java.util.TimeZone;

import play.data.validation.Email;
import play.data.validation.Required;
import siena.Filter;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.NotNull;
import siena.Query;

public class User extends Model {
	
	@Id(Generator.AUTO_INCREMENT)
	public Long id;
	
	/** Display name */
	@Required
	@NotNull
	public String name;
	
	/** Email identifying the user */
	@Required @Email
	@NotNull
	public String email;
	
	/** User TimeZone */
	@Required
	@NotNull
	//public TimeZone tz;
	public String tz;
	
	/** Remember the user in a cookie */
	@Required
	@NotNull
	public boolean rememberMe;
	
	/** List of threads followed by the user */
	@Filter("user")
	public Query<Following> followedThreads;
	
	/**
	 * Ideas of profile settings:
	 *  - Hide or show all unread posts
	 *  - Automatically follow replied threads
	 *  - Number of posts by page
	 */
	
	public User(String name, String email, String tz, boolean rememberMe) {
		this.name = name;
		this.email = email;
		this.tz = tz;
		this.rememberMe = rememberMe;
	}
	
	public static Query<User> all() {
		return Model.all(User.class);
	}
	
	public static User findByEmail(String email) {
		return User.all().filter("email", email).get();
	}
	
	/**
	 * Update user attributes
	 */
	public void update(Update update) {
		name = update.name;
		tz = update.tzId;
		rememberMe = update.rememberMe;
		update();
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
		Reading reading = Reading.all().filter("thread", thread.id).filter("user", this.id).get();
		if (reading == null) { // Creates the reading if it doesn't exist
			reading = new Reading(thread, this, new Date());
			reading.insert();
		}
		return reading;
	}
	
	/**
	 * Follow a given thread
	 * @param thread Thread to be followed
	 */
	public void follow(Thread thread) {
		if (followedThreads.filter("thread", thread.id).filter("user", this.id).count() == 0) {
			Following following = new Following(thread, this);
			following.insert();
		}
	}
	
	/**
	 * Remove a given thread from the followed threads set
	 * @param thread
	 */
	public void doNotFollow(Thread thread) {
		Following following = followedThreads.filter("thread", thread.id).filter("user", this.id).get();
		if (following != null) {
			following.delete();
		}
	}
	
	/**
	 * Get the number of posts unread by this user in a given thread
	 * @param thread
	 * @return
	 */
	public long getUnreadPostCount(Thread thread) {
		return thread.getPostCountAfter(lastReading(thread).date);
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
			tzId = user.tz;
			rememberMe = user.rememberMe;
		}
	}
}
