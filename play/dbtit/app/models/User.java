package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Query;

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
	@ManyToMany @OrderBy("lastActivity DESC")
	public List<Thread> followedThreads;
	
	/** List of the post read by the user */
	@ManyToMany
	public List<Post> readPosts;
	
	/** List of the rooms this user subscribed to */
	@ManyToMany
	public List<Room> subscriptions;
	
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
		this.readPosts = new ArrayList<Post>();
		this.subscriptions = new ArrayList<Room>();
	}
	
	public static User create(String name, String email, String tz) {
		User user = new User(name, email, tz);
		user.save();
		//user.subscribe(Room.getOpenRoom()); // FIXME
		return user;
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
		return subscriptions.contains(room);
	}
	
	public void subscribe(Room room) {
		if (!hasSubscribed(room)) {
			subscriptions.add(room);
			save(); // FIXME Ce save est-il nécessaire ? Est-il possible de le sortir du modèle ?
		}
	}
	
	public void unsubscribe(Room room) {
		if (hasSubscribed(room)) {
			subscriptions.remove(room);
			save();
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
			readPosts.add(post);
			save();
		}
	}
	
	/**
	 * Mark the given post as unread
	 * @param post
	 */
	public void unread(Post post) {
		if (hasRead(post)) {
			readPosts.remove(post);
			save();
		}
	}
	
	/**
	 * @return true if this user has read the given post
	 */
	public boolean hasRead(Post post) {
		return readPosts.contains(post);
	}
	
	/**
	 * Get the number of posts unread by this user in a given thread
	 * @param thread
	 * @return
	 */
	public long getUnreadPostCount(Thread thread) {
		return Post.count("thread = ? and not ? member of e.readers", thread, this);
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
