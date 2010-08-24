package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import siena.Column;
import siena.Filter;
import siena.Id;
import siena.Model;
import siena.NotNull;
import siena.Query;
import siena.Text;

public class Paragraph extends Model {
	
	@Id
	public Long id;

	/** HTML content of the paragraph */
	@Required @MaxSize(1000)
	@Text @NotNull
	public String content;
	
	/** Answers of the paragraph */
	@Filter("parent")
	public Query<Post> answers;
	
	/** Footnotes referenced by this post */
	@Filter("paragraph")
	public Query<FootNote> footNotes;
	
	/** Post which this paragraph belongs to */
	@Required
	@NotNull @Column("post")
	public Post post;
	
	/** Number of the post (added to sort paragraphs) */
	public Integer number;
	
	public Paragraph(Post post, String content, Integer number)
	{
		this.post = post;
		this.content = content;
		this.number = number;
	}
	
	public static Query<Paragraph> all() {
		return Model.all(Paragraph.class);
	}
	
	public static Paragraph findById(Long id) {
		return Paragraph.all().filter("id", id).get();
	}
	
	@Override
	public String toString() {
		return content.substring(0, Math.min(content.length(), 40));
	}
	
	/**
	 * Reply to this paragraph
	 * @param author Author of the reply
	 * @param content Content of the reply post
	 */
	public Post reply(User author, String content) {
		post.get();
		Thread thread = post.thread;
		Post reply = Post.create(author, content, this, thread);
		author.follow(thread);
		return reply;
	}
	
	public boolean hasAnswers() {
		return answers.count() != 0;
	}

	/**
	 * Convenient function to add a footnote to a paragraph
	 */
	public void addFootNote(FootNote footNote) {
		footNote.paragraph = this;
		footNote.update();
	}
	
	public boolean hasFootNotes() {
		return footNotes.count() != 0;
	}
}
