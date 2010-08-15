package models;

import siena.Generator;
import siena.Id;
import siena.Model;
import siena.NotNull;
import siena.Query;

public class Following extends Model {

	@Id(Generator.AUTO_INCREMENT)
	public Long id;
	
	@NotNull
	public User user;
	
	@NotNull
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
