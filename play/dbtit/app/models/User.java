package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import play.data.validation.Email;
import play.data.validation.Required;
import siena.Filter;
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
	
	/** User preference */
	public Boolean exploreUnreadPosts;
	
	/** List of threads followed by the user */
	@Filter("user")
	public Query<Following> followedThreads;
	
	/** List of the post read by the user */
	@Filter("user")
	public Query<Reading> readPosts;
	
	/** List of the rooms this user subscribed to */
	@Filter("user")
	public Query<RoomSubscription> subscriptions;
	
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
	}
	
	public static Query<User> all() {
		return Model.all(User.class);
	}
	
	public static User findByEmail(String email) {
		return User.all().filter("email", email).get();
	}
	
	public static User create(String name, String email, String tz) {
		User user = new User(name, email, tz);
		user.insert();
		user.subscribe(Room.getOpenRoom());
		return user;
	}
	
	public List<Thread> followedThreads() {
		List<Following> followings = followedThreads.fetch();
		List<Thread> threads = new ArrayList<Thread>();
		for (Following f : followings) {
			f.thread.get();
			threads.add(f.thread);
		}
		return Thread.sortByLastPost(threads);
	}
	
	/**
	 * Update user attributes
	 */
	public void updateProfile(Update update) {
		name = update.name;
		tz = update.tzId;
		exploreUnreadPosts = !update.stopOnUnreadPosts;
		update();
	}
	
	public boolean hasSubscribed(Room room) {
		return subscriptions.filter("room", room).count() != 0;
	}
	
	public void subscribe(Room room) {
		if (!hasSubscribed(room)) {
			RoomSubscription subscription = new RoomSubscription(room, this);
			subscription.insert();
		}
	}
	
	public void unsubscribe(Room room) {
		RoomSubscription subscription = subscriptions.filter("room", room).get();
		if (subscription != null) {
			subscription.delete();
		}
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
	
	/**
	 * @return true if this user is following the given thread
	 */
	public boolean isFollowing(Thread thread) {
		return followedThreads.filter("thread", thread).count() != 0;
	}
	
	
	/**
	 * Mark the given post as read
	 * @param post
	 */
	public void read(Post post) {
		if (!hasRead(post)) {
			Reading reading = new Reading(post, this);
			reading.insert();
		}
	}
	
	/**
	 * Mark the given post as unread
	 * @param post
	 */
	public void unread(Post post) {
		Reading reading = readPosts.filter("post", post).get();
		if (reading != null) {
			reading.delete();
		}
	}
	
	/**
	 * @return true if this user has read the given post
	 */
	public boolean hasRead(Post post) {
		return readPosts.filter("post", post).count() != 0;
	}
	
	/**
	 * Get the number of posts unread by this user in a given thread
	 * @param thread
	 * @return
	 */
	public long getUnreadPostCount(Thread thread) {
		return Post.all().filter("thread", thread).count() - readPosts.filter("thread", thread).count();
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
