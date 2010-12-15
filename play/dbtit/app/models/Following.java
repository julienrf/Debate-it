package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Following extends Model {

	@Required
	@ManyToOne
	public User user;
	
	@Required
	@ManyToOne
	public Thread thread;
	
	public Following(Thread thread, User user) {
		this.thread = thread;
		this.user = user;
	}

	/*public static Following findByThreadAndUser(Thread thread, User user) {
		return Following.all(Following.class).filter("thread", thread.id).filter("user", user.id).get();
	}*/
}
