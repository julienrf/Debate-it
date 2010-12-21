package dbtit;

import java.util.TimeZone;

import models.Post;
import models.Room;
import models.Thread;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class ThreadTest extends UnitTest {

	private User user;
	private User creator;
	private Room room;
	private Thread thread;

	@Before
	public void setUp() throws Exception {
		user = User.create("Julien Richard-Foy", "julien.rf@no-log.org", TimeZone.getDefault().getID());
		creator = User.create("Creator", "foo@bar.bz", TimeZone.getDefault().getID());
		room = Room.create(creator, "Room", false);
		thread = Thread.create(creator, room, "Title", "Content");
	}

	@Test
	public void testCreateUserRoomStringString() {
		Thread.create(user, room, "Titre", "Contenu");
		
		Thread thread = Thread.find("byTitle", "Titre").first();
		assertNotNull(thread);
		assertNotNull(thread.rootPost);
		assertEquals("Contenu", thread.rootPost.content);
	}

	@Test
	public void testSortByLastPost() {
		// TODO ?
	}

	@Test
	public void testGetPostCountAfter() {
		assertEquals(0, thread.getPostCountAfter(thread.rootPost.date));
		thread.rootPost.paragraphs.get(0).reply(user, "Plop");
		assertEquals(1, thread.getPostCountAfter(thread.rootPost.date));
	}

	@Test
	public void testLastPost() {
		Post post = thread.rootPost.paragraphs.get(0).reply(user, "Plop");
		assertSame(post, thread.lastPost());
		assertNotSame(thread.rootPost, thread.lastPost());
	}

}
