package models;

import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;

public class Following extends Model {

	@Id
	public Long id;
	
	@Index("user_index")
	public User user;
	
	@Index("thread_index")
	public Thread thread;
	
	public Following(Thread thread, User user) {
		this.thread = thread;
		this.user = user;
	}
	
	public static Query<Following> all() {
		return Model.all(Following.class);
	}
}
