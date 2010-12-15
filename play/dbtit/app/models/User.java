package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import controllers.admin.Readings;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {
	
	/** Display name */
	@Required
	public String name;
	
	/** Email identifying the user */
	@Required @Email
	public String email;
	
	/** User TimeZone */
	@Required
	public String tz;
	
	/** User preference */
	public Boolean exploreUnreadPosts;
	
	/** List of threads followed by the user */
	@OneToMany // FIXME Pourquoi ce n’est pas du ManyToMany ?
	public List<Thread> followedThreads;
	
	/** List of the post read by the user */
	@OneToMany(mappedBy="user", cascade=CascadeType.REMOVE)
	public List<Reading> readPosts;
	
	/** List of the rooms this user subscribed to */
	@OneToMany(mappedBy="user", cascade=CascadeType.REMOVE)
	public List<RoomSubscription> subscriptions;
	
	/**
	 * Ideas of profile settings:
	 *  - Hide or show all unread posts
	 *  - Automatically follow replied threads
	 *  - Number of posts by page
	 */
	
	protected User(String name, String email, String tz) {
		this.name = name;
		this.email = email;
		this.tz = tz;
		this.exploreUnreadPosts = false;
		this.followedThreads = new ArrayList<Thread>();
		this.readPosts = new ArrayList<Reading>();
		this.subscriptions = new ArrayList<RoomSubscription>();
	}
	
	public static User create(String name, String email, String tz) {
		User user = new User(name, email, tz);
		user.save();
		user.subscribe(Room.getOpenRoom()); // FIXME Supprimer ça ?
		return user;
	}
	
	// TODO il doit y avoir moyen de transformer ça en requête plus efficace
	public List<Thread> followedThreads() {
		/*List<Thread> threads = new ArrayList<Thread>();
		for (Following f : followedThreads) {
			threads.add(f.thread);
		}*/
		return Thread.sortByLastPost(followedThreads);
	}
	
	/**
	 * Update user attributes
	 */
	public void updateProfile(Update update) {
		name = update.name;
		tz = update.tzId;
		exploreUnreadPosts = !update.stopOnUnreadPosts;
		save();
	}
	
	public boolean hasSubscribed(Room room) {
		return RoomSubscription.find("byUserAndRoom", this, room).fetch().size() != 0; // TODO Avec une relation ManyToMany j’aurais juste à faire un subscriptions.contains(room)
		//return subscriptions.filter("room", room).count() != 0;
	}
	
	public void subscribe(Room room) {
		if (!hasSubscribed(room)) {
			RoomSubscription subscription = new RoomSubscription(room, this);
			subscription.save();
		}
	}
	
	public void unsubscribe(Room room) {
		RoomSubscription subscription = RoomSubscription.find("byUserAndRoom", this, room).first();
		if (subscription != null) {
			subscription.delete();
		}
	}
	
	/**
	 * Follow a given thread
	 * @param thread Thread to be followed
	 */
	public void follow(Thread thread) {
		if (!followedThreads.contains(thread)) {
			followedThreads.add(thread);
			save();
		}
	}
	
	/**
	 * Remove a given thread from the followed threads set
	 * @param thread
	 */
	public void doNotFollow(Thread thread) {
		if (followedThreads.contains(thread)) {
			followedThreads.remove(thread);
			save();
		}
	}
	
	/**
	 * @return true if this user is following the given thread
	 */
	public boolean isFollowing(Thread thread) {
		return followedThreads.contains(thread);
	}
	
	
	/**
	 * Mark the given post as read
	 * @param post
	 */
	public void read(Post post) {
		if (!hasRead(post)) {
			Reading reading = new Reading(post, this);
			reading.save();
			readPosts.add(reading);
		}
	}
	
	/**
	 * Mark the given post as unread
	 * @param post
	 */
	public void unread(Post post) {
		Reading reading = Reading.find("byUserAndPost", this, post).first();
		if (reading != null) {
			reading.delete();
		}
	}
	
	/**
	 * @return true if this user has read the given post
	 */
	public boolean hasRead(Post post) {
		return Reading.count("user = ? and post = ?", this, post) != 0;
	}
	
	/**
	 * Get the number of posts unread by this user in a given thread
	 * @param thread
	 * @return
	 */
	public long getUnreadPostCount(Thread thread) {
		return Post.count("thread = ?", thread) - Reading.count("thread = ? and user = ?", thread, this);
	}
	
	
	public String toString() {
		return name;
	}

	public static class Update {
		@Required public String name;
		@Required public String tzId;
		@Required public boolean stopOnUnreadPosts;
		
		public Update(User user) {
			name = user.name;
			tzId = user.tz;
			stopOnUnreadPosts = !user.exploreUnreadPosts;
		}
	}
}
