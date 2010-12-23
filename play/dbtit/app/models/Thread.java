package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.Play;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.Codec;
import utils.Helper;

@Entity
public class Thread extends Model {
	
	/** The initial post of the thread */
	@OneToOne
	public Post rootPost;
	
	/** The topic of the thread */
	@Required
	public String title;
	
	/** A hash code identifying the thread */
	public String hash;
	
	/** The room which this thread belongs to */
	@Required
	@ManyToOne
	public Room room;
	
	/**
	 * The date of the last activity (posting) in this thread (this date is stored to sort more easily threads by activity date)
	 * This class expects that created posts will update the lastActivity of their thread.
	 */
	public Date lastActivity;
	
	/** Protected constructor since the static helper should be used, for better consistency */
	protected Thread(Room room, String title)
	{
		this.title = title;
		this.room = room;
		room.threads.add(this);
	}
	
	public String toString() {
		return title;
	}
	
	/**
	 * Convenient function to create a new thread.
	 * It creates the thread and its root post and saves it on the persistence layer.
	 * @param author Author of the root post
	 * @param room The room which the thread belongs to
	 * @param title Title of the thread
	 * @param content Content of the root post of the thread
	 * @return The created thread
	 */
	public static Thread create(User author, Room room, String title, String content)
	{
		Thread thread = new Thread(room, title).save();
		thread.hash = Helper.hexTo62(Codec.hexMD5(Play.secretKey + thread.id));
		Post rootPost = Post.create(author, content, null, thread);
		thread.rootPost = rootPost;
		thread.save();
		return thread;
	}
	
	/**
	 * Count the number of posts posted after a given date in this thread
	 * @param date
	 * @return
	 */
	public long getPostCountAfter(Date date) {
		return Post.find("thread = ? and date > ?", this, date).fetch().size();
	}
	
	public Post lastPost() {
		return Post.find("thread = ? order by date desc", this).first();
	}
}
