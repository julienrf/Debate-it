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

public class ParagraphTest extends UnitTest {

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
	public void testReply() {
		Paragraph p = thread.rootPost.paragraphs.get(0);
		Post reply = p.reply(author, "content");
		
		assertTrue(p.hasAnswers());
		assertTrue(p.answers.contains(reply));
	}
}
