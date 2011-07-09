package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import etoffe.NumberGenerator;
import etoffe.java.EtoffeResult;
import etoffe.java.JEtoffe;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Post extends Model {
    /**
     * Author of the post
     */
    @Required
    @ManyToOne
    public User author;

    /**
     * Date when the message was posted
     */
    @Required
    public Date date;

    /**
     * The paragraph that this post answers
     */
    @ManyToOne
    public Paragraph parent;

    /**
     * Raw content of the whole post (in Parsit syntax)
     */
    @Required
    @MaxSize(10000)
    @Lob
    public String content;

    /**
     * The paragraphs of this post
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    @OrderBy("number ASC")
    public List<Paragraph> paragraphs;

    /**
     * The thread that this post belongs to
     */
    @Required
    @ManyToOne
    public Thread thread;

    /**
     * Who has read this post?
     */
    @ManyToMany(mappedBy = "readPosts")
    Collection<User> readers;

    /**
     * Protected constructor since the static helper should be used for better consistency
     *
     * @param author
     * @param parent
     * @param thread
     * @param date
     */
    protected Post(User author, Paragraph parent, Thread thread, Date date) {
        this.thread = thread;
        this.author = author;
        this.parent = parent;
        this.date = date;
        this.paragraphs = new ArrayList<Paragraph>();
        this.readers = new HashSet<User>();
    }

    /**
     * Convenient function to create a post.
     *
     * @param author  Author of the  post
     * @param content Content of the post
     * @param parent  Parent post
     * @return The created post
     */
    public static Post create(User author, String content, Paragraph parent, Thread thread) {
        // Create the post
        Post post = new Post(author, parent, thread, new Date());
        if (parent != null)
            parent.answers.add(post);
        post.save(); // The save is needed because the paragraphs about to be created will reference this post
        post.setParagraphs(content);

        // Update the activity of the thread
        thread.updateLastActivity(post.date);
        thread.save();

        // Make the author automatically follows the post's thread
        author.follow(thread);
        author.read(post);

        return post;
    }

    @Override
    public String toString() {
        String content = paragraphs.get(0).content; // A post must have at least one paragraph
        return content.substring(0, Math.min(content.length(), 50));
    }

    // TODO make this method non static
    public static void preview(String content, List<String> paragraphs, List<FootNote> footNotes) {
        final EtoffeResult result = JEtoffe.renderParagraphs(content, new NumberGenerator());
        for (String p : result.paragraphs()) {
            paragraphs.add(p);
        }
        for (String footNote : result.footnotes()) {
            footNotes.add(new FootNote(footNote));
        }
    }

    /**
     * Change the content of the post
     *
     * @param content
     */
    public void edit(String content) {
        setParagraphs(content);
    }

    /**
     * Change the content of the post
     * The actual paragraphs will be removed and new paragraphs will
     * be created from the content.
     *
     * @param content New content.
     */
    private void setParagraphs(String content) {
        this.content = content;

        // Remove current paragraphs and footnotes
        for (Paragraph paragraph : paragraphs) {
            paragraph.delete();
        }
        paragraphs.clear();

        // Parse content
        final EtoffeResult result = JEtoffe.renderParagraphs(content, new NumberGenerator());
        // Set new paragraphs
        int number = 0;
        for (String p : result.paragraphs()) {
            Paragraph paragraph = new Paragraph(this, p, number++).save();
            for (String footNote : result.footnotes()) { // FIXME Why do is this loop nested in the paragraphs loop?
                paragraph.addFootNote(new FootNote(footNote));
            }
            paragraphs.add(paragraph);
        }
        this.save();
    }

    /**
     * Check if this post has answers
     *
     * @return true if at least one of its paragraph has at least one answer.
     */
    public boolean hasAnswers() {
        for (Paragraph paragraph : paragraphs) {
            if (paragraph.hasAnswers())
                return true;
        }
        return false;
    }
}
