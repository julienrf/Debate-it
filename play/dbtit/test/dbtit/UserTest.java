package dbtit;

import java.util.TimeZone;

import models.Room;
import models.Thread;
import models.User;
import models.User.Update;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends UnitTest {
	
	private User user;
	private User creator;
	private Room room;
	private Thread thread;

	@Before
	public void setUp() throws Exception {
		Fixtures.deleteAll();
		user = User.create("Julien Richard-Foy", "julien.rf@no-log.org", TimeZone.getDefault().getID());
		creator = User.create("Creator", "foo@bar.bz", TimeZone.getDefault().getID());
		room = Room.create(creator, "Room", false);
		thread = Thread.create(creator, room, "Title", "Content", new String[0]);
	}

	@Test
	public void testCreate() {
		User.create("Julien RF", "julien@richard-foy.fr", TimeZone.getDefault().getID());
		
		User user = User.find("byEmail", "julien@richard-foy.fr").first();
		assertNotNull(user);
		assertEquals("Julien RF", user.name);
	}

	@Test
	public void testUpdateProfile() {
		assertEquals("Julien Richard-Foy", user.name);
		
		Update update = new Update(user);
		update.name = "Julien RF";
		user.updateProfile(update);
		
		assertEquals("Julien RF", user.name);
	}

	@Test
	public void testSubscribe() {
		assertFalse(user.hasSubscribed(room));
		
		user.subscribe(room);
		
		assertTrue(user.hasSubscribed(room));
	}

	@Test
	public void testUnsubscribe() {
		user.subscribe(room);
		assertTrue(user.hasSubscribed(room));
		
		user.unsubscribe(room);
		assertFalse(user.hasSubscribed(room));
	}

	@Test
	public void testFollow() {
		assertFalse(user.isFollowing(thread));
		
		user.follow(thread);
		
		assertTrue(user.isFollowing(thread));
	}

	@Test
	public void testDoNotFollow() {
		user.follow(thread);
		assertTrue(user.isFollowing(thread));
		
		user.doNotFollow(thread );
		
		assertFalse(user.isFollowing(thread));
	}

	@Test
	public void testRead() {
		assertFalse(user.hasRead(thread.rootPost));
		
		user.read(thread.rootPost);
		
		assertTrue(user.hasRead(thread.rootPost));
	}

	@Test
	public void testUnread() {
		user.read(thread.rootPost);
		assertTrue(user.hasRead(thread.rootPost));
		
		user.unread(thread.rootPost);
		assertFalse(user.hasRead(thread.rootPost));
	}

	@Test
	public void testGetUnreadPostCount() {
		assertEquals(1, user.getUnreadPostCount(thread));
		
		thread.rootPost.paragraphs.get(0).reply(creator, "Foo");
		
		assertEquals(2, user.getUnreadPostCount(thread));
	}

}
