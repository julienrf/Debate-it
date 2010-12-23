package dbtit;

import java.util.TimeZone;

import models.Room;
import models.Thread;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class RoomTest extends UnitTest {

	private User creator;
	private Room room;

	@Before
	public void setUp() throws Exception {
		Fixtures.deleteAll();
		creator = User.create("Creator", "foo@bar.bz", TimeZone.getDefault().getID());
		room = Room.create(creator, "Room", false);
	}

	@Test
	public void testGetOpenRoom() {
		Room room = Room.getOpenRoom();
		assertEquals("open", room.hash);
		assertEquals("Open Room", room.name);
		assertTrue(room.isPublic);
	}

	@Test
	public void testLastActivity() {
		Thread thread1 = Thread.create(creator, room, "Title", "Content");
		assertEquals(thread1, room.threads.get(0));
		Thread thread2 = Thread.create(creator, room, "Titre", "Contenu");
		room.refresh();
		assertEquals(thread2, room.threads.get(0));
	}

}
