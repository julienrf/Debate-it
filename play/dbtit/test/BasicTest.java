import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;
import models.Thread;

/**
 * TODO
 * Faire vraiment des tests parce que là l’appli commence à grossir…
 * 
 * - Créer un thread
 * - Éditer
 * - Répondre
 * - Modifier son profil
 * - Login automatique
 * @author julien
 *
 */
public class BasicTest extends UnitTest {
	
	User julien;

	@Before
	public void setup() {
		Fixtures.deleteAll();
		
		for (Following f : Following.all().fetch())
			f.delete();
		
		for (FootNote f : FootNote.all().fetch())
			f.delete();
		
		for (Paragraph p : Paragraph.all().fetch())
			p.delete();
		
		for (Post p : Post.all().fetch())
			p.delete();
		
		for (Reading r : Reading.all().fetch())
			r.delete();
		
		for (Thread t : Thread.all().fetch())
			t.delete();
		
		for (User u : User.all().fetch())
			u.delete();
		
		julien = User.create("Julien Richard-Foy", "julien.rf@no-log.org", TimeZone.getDefault().getID());
		julien.insert();
	}
	
	@Test
	public void user()
	{
		User user = User.findByEmail("julien.rf@no-log.org");
		assertNotNull(user);
		assertEquals("Julien Richard-Foy", user.name);
	}
	
	@Test
	public void post()
	{
		Post post = Post.create(julien, "Contenu", null, null);

		post = Post.all().filter("parent", null).get();
		assertNotNull(post);
		assertEquals("Contenu", post.content);
		assertEquals(post.paragraphs.count(), 1);
		assertEquals(post.paragraphs.get().content, "Contenu");
	}
	
	@Test
	public void reply()
	{
		Post post = Post.create(julien, "Contenu", null, null);

		Paragraph paragraph = post.paragraphs.get();
		paragraph.reply(julien, "Reply");
		
		Post reply = Post.all().filter("parent", paragraph).get();
		assertNotNull(reply);
		assertEquals("Reply", reply.content);
	}
	
	@Test
	public void edit()
	{
		Post post = Post.create(julien, "Contenu", null, null);

		post.edit("Nouveau contenu");
		
		assertEquals("Nouveau contenu", post.content);
		assertEquals(post.paragraphs.count(), 1);
		
		post.edit("Nouveau\n\ncontenu");
		
		assertEquals(post.paragraphs.count(), 2);
	}
	
	@Test
	public void rooms()
	{
		Room room = Room.create(julien, "Test room"/*, true*/);
		
		assertTrue(julien.hasSubscribed(room));
		
		julien.unsubscribe(room);
		
		assertFalse(julien.hasSubscribed(room));
	}
	
	@Test
	public void threads()
	{
		Room room = Room.create(julien, "test room"/*, true*/);
		Thread thread = Thread.create(julien, room, "Titre", "Contenu");

		thread = Thread.all().filter("title", "Titre").get();
		assertNotNull(thread);
		assertNotNull(thread.rootPost);
		thread.rootPost.get();
		assertEquals("Contenu", thread.rootPost.content);
		
		Paragraph paragraph = thread.rootPost.paragraphs.get();
		paragraph.reply(julien, "Reply");
		
		Post reply = Post.all().filter("parent", paragraph).get();
		assertNotNull(reply);
		assertEquals("Reply", reply.content);
	}
	
	@Test
	public void follow() {
		Room room = Room.create(julien, "test room"/*, true*/);
		Thread thread = Thread.create(julien, room, "Titre", "Contenu");
		
		assertEquals(1, julien.followedThreads.count());
		assertTrue(julien.isFollowing(thread));
		
		julien.doNotFollow(thread);
		
		assertEquals(0, julien.followedThreads.count());
		assertFalse(julien.isFollowing(thread));
		
		julien.follow(thread);
		
		assertEquals(1, julien.followedThreads.count());
		assertTrue(julien.isFollowing(thread));
	}
	
	@Test
	public void read() {
		Room room = Room.create(julien, "test room"/*, true*/);
		Thread thread = Thread.create(julien, room, "Titre", "Contenu");
		
		assertTrue(julien.hasRead(thread.rootPost));
		
		julien.read(thread.rootPost);
		assertTrue(julien.hasRead(thread.rootPost));
		
		julien.unread(thread.rootPost);
		assertFalse(julien.hasRead(thread.rootPost));
	}
}
