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
	
	@Id
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
	public String tz;
	
	/** List of threads followed by the user */
	@Filter("user")
	public Query<Following> followedThreads;
	
	@Filter("user")
	public Query<Reading> readings;
	
	/**
	 * Ideas of profile settings:
	 *  - Hide or show all unread posts
	 *  - Automatically follow replied threads
	 *  - Number of posts by page
	 */
	
	public User(String name, String email, String tz) {
		this.name = name;
		this.email = email;
		this.tz = tz;
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
		Reading reading = readings.filter("thread", thread).get();
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
		if (!isFollowing(thread)) {
			Following following = new Following(thread, this);
			following.insert();
		}
	}
	
	/**
	 * Remove a given thread from the followed threads set
	 * @param thread
	 */
	public void doNotFollow(Thread thread) {
		Following following = followedThreads.filter("thread", thread).get();
		if (following != null) {
			following.delete();
		}
	}
	
	public boolean isFollowing(Thread thread) {
		return followedThreads.filter("thread", thread).count() != 0;
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
		
		public Update(User user) {
			name = user.name;
			tzId = user.tz;
		}
	}
}
