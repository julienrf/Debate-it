package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import siena.Filter;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;

public class Paragraph extends Model {
	
	@Id
	public Long id;

	/** HTML content of the paragraph */
	@Required
	@MaxSize(1000)
	public String content;
	
	/** Answers of the paragraph */
	@Filter("parent")
	public Query<Post> answers;
	
	/** Footnotes referenced by this post */
	@Filter("paragraph")
	public Query<FootNote> footNotes;
	
	/** Post which this paragraph belongs to */
	@Required
	@Index("post_index")
	public Post post;
	
	/** Number of the post (added to sort paragraphs, FIXME remove it with Siena) */
	public Integer number;
	
	public Paragraph(Post post, String content, Integer number)
	{
		this.post = post;
		this.content = content;
		//this.answers = new ArrayList<Post>();
		//this.footNotes = new ArrayList<FootNote>();
		this.number = number;
	}
	
	public static Query<Paragraph> all() {
		return Model.all(Paragraph.class);
	}
	
	public static Paragraph findById(Long id) {
		for (Paragraph p : Paragraph.all().fetch()) {
			System.out.println("p: " + p.id);
		}
		System.out.println(Paragraph.all().filter("id", id).get());
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
		Post post = this.post;
		post.get();
		Thread thread = post.thread;
		thread.get();
		Post reply = Post.create(author, content, this, thread);
		//this.answers.add(post);
		author.follow(thread);
		return reply;
	}

	/**
	 * Convenient function to add a footnote to a paragraph
	 */
	public void addFootNote(FootNote footNote) {
		footNote.paragraph = this;
		//footNotes.add(footNote);
		footNote.update();
	}
	
	public void getParagraphAndAnswersBefore(Date lastReading, Reading reading, Set<Paragraph> paragraphs, List<FootNote> footNotesList) {
		paragraphs.add(this);
		footNotesList.addAll(footNotes.fetch());
		getAnsweredParagraphsBefore(lastReading, reading, paragraphs, footNotesList);
	}
	
	/**
	 * Collect the answers of this paragraph and try to add it to the set of paragraph unless their date is further than lastReading.
	 * @param lastReading deadline
	 * @param reading optionnal reading object which will be updated according to the date of the collected unread posts
	 * @param paragraphs set of paragraphs which will be filled with the collected paragraphs
	 * @param footNotesList list of footnotes which will be filled with the footnotes of the collected paragraphs, excluding this paragraph
	 */
	public void getAnsweredParagraphsBefore(Date lastReading, Reading reading, Set<Paragraph> paragraphs, List<FootNote> footNotesList) {
		if (answers.count() != 0) {
			paragraphs.add(this); // This paragraph contains answers, add it to the list of answered paragraphs
			for (Post answer : answers.fetch()) {
				answer.getAnsweredParagraphsBefore(lastReading, reading, paragraphs, footNotesList);
				if (reading != null) { // This answer is after the last post the user has already read
					reading.updateDate(answer.date);
				}
			}
		}
	}
}
