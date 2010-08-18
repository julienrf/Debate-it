package models;

import siena.Column;
import siena.Id;
import siena.Model;
import siena.NotNull;
import siena.Query;

public class Following extends Model {

	@Id
	public Long id;
	
	@NotNull @Column("user")
	public User user;
	
	@NotNull @Column("thread")
	public Thread thread;
	
	public Following(Thread thread, User user) {
		this.thread = thread;
		this.user = user;
	}
	
	public static Query<Following> all() {
		return Model.all(Following.class);
	}
	
	/*public static Following findByThreadAndUser(Thread thread, User user) {
		return Following.all(Following.class).filter("thread", thread.id).filter("user", user.id).get();
	}*/
}
