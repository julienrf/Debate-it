package dbtit;

import java.util.TimeZone;

import models.Paragraph;
import models.Post;
import models.Room;
import models.Thread;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class PostTest extends UnitTest {

	private Thread thread;
	private User author;
	private Room room;

	@Before
	public void setUp() throws Exception {
		author = User.create("Creator", "foo@bar.bz", TimeZone.getDefault().getID());
		room = Room.create(author, "Room", false);
		thread = Thread.create(author, room, "Title", "Content");
	}

	@Test
	public void testCreateUserStringParagraphThread() {
		Paragraph paragraph = thread.rootPost.paragraphs.get(0);
		assertFalse(paragraph.hasAnswers());
		
		Post post = Post.create(author, "Content", paragraph, thread);
		assertTrue(paragraph.hasAnswers());
		assertTrue(paragraph.answers.contains(post));
	}

	@Test
	public void testPreview() {
		// TODO ?
		//fail("Not yet implemented");
	}

	@Test
	public void testEditString() {
		// TODO
		//fail("Not yet implemented");
	}
}
