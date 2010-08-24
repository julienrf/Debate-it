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
	@NotNull @Column("post")
	public Post post;
	
	@Required
	@NotNull @Column("user")
	public User user;
	
	@Column("thread")
	public Thread thread; // Needed by the getUnreadPostCount query (unless I'm wrong?)
	
	public Reading(Post post, User user)
	{
		this.post = post;
		this.user = user;
		this.thread = post.thread;
	}
	
	public static Query<Reading> all() {
		return Model.all(Reading.class);
	}
	
	public String toString() {
		return "" + user + "-" + post;
	}
}
