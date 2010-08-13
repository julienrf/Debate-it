package models;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

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
	
	/** A number generator used to salt the hash computation */
	@Transient
	private static AtomicLong generator = new AtomicLong(System.currentTimeMillis());
	
	/*@OneToMany(mappedBy = "thread", cascade = CascadeType.ALL)
	public List<Reading> readings;*/
	
	/** Protected constructor since the static helper should be used, for better consistency */
	protected Thread(String title)
	{
		this.title = title;
		this.hash = Helper.hexTo62(Codec.hexMD5(title + generator.incrementAndGet()));
		this.rootPost = null;
		//this.readings = new ArrayList<Reading>();
	}
	
	public String toString() {
		return title;
	}
	
	/**
	 * Convenient function to create a new thread.
	 * It creates the thread and its root post and saves it on the persistence layer.
	 * @param author Author of the root post
	 * @param title Title of the thread
	 * @param content Contente of the root post of the thread
	 * @return The created thread
	 */
	public static Thread create(User author, String title, String content)
	{
		Thread thread = new Thread(title).save();
		Post rootPost = Post.create(author, content, null, thread);
		thread.rootPost = rootPost;
		thread.save();
		author.follow(thread);
		return thread;
	}
	
	/**
	 * TODO TESTS
	 * Donne la liste de tous les paragraphes qui contiennent des réponses et qui ont été écrits avant une date
	 * @param reading Contains the date of the last post read in this thread by the user
	 * @param footNotes A footnotes container which will be filled with posts footnotes
	 */
	public void getAnsweredParagraphsBefore(Date lastReading, Reading reading, Set<Paragraph> paragraphsToShow, List<FootNote> footNotesList) {
		rootPost.getAnsweredParagraphsBefore(lastReading, reading, paragraphsToShow, footNotesList);
		if (reading != null) {
			reading.updateDate(rootPost.date);
		}
	}
	
	/**
	 * Count the number of posts posted after a given date in this thread
	 * @param date
	 * @return
	 */
	public long getPostCountAfter(Date date) {
		return Post.count("thread = ? and date > ?", this, date);
	}
}
