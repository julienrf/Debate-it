package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * A reading is a triplet linking a user and a thread with the date of the last post the user has read in the thread
 * TODO On peut très bien se contenter de couples (post, user) (donc d’une relation @ManyToMany)
 * @author julien
 *
 */
@Entity
public class Reading extends Model {
	
	@Required
	@ManyToOne
	public Post post;
	
	@Required
	@ManyToOne
	public User user;
	
	@Required
	@ManyToOne
	public Thread thread; // FIXME Needed by the getUnreadPostCount query (unless I'm wrong?)
	
	public Reading(Post post, User user) {
		this.post = post;
		this.user = user;
		this.thread = post.thread;
	}
	
	public String toString() {
		return "" + user + "-" + post;
	}
}
