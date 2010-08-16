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
		
		for (Thread thread : Thread.all().fetch()) {
			thread.delete();
		}
		
		for (Post post : Post.all().fetch()) {
			post.delete();
		}
		
		for (Paragraph p : Paragraph.all().fetch()) {
			p.delete();
		}
		
		for (User user : User.all().fetch()) {
			user.delete();
		}
		
		for (FootNote f : FootNote.all().fetch()) {
			f.delete();
		}
		
		for (Reading r : Reading.all().fetch()) {
			r.delete();
		}
		
		for (Following f : Following.all().fetch()) {
			f.delete();
		}
		
		for (A a : A.all().fetch()) {
			a.delete();
		}
		
		for (B b : B.all().fetch()) {
			b.delete();
		}
		
		for (C c : C.all().fetch()) {
			c.delete();
		}
		
		julien = new User("Julien Richard-Foy", "julien.rf@no-log.org", "Europe/Paris", false);
		julien.insert();
	}
	
	@Test
	public void user()
	{
		assertNotNull(julien);
		User user = User.all().filter("email", "julien.rf@no-log.org").get();
		assertNotNull(user);
		assertEquals("Julien Richard-Foy", user.name);
	}
	
	@Test
	public void post()
	{
		Thread thread = Thread.create(julien, "Titre1", "Contenu");
		Post post = thread.rootPost;
		post.get();

		assertNotNull(post);
		assertEquals("Contenu", post.content);
		assertEquals(1, post.paragraphs.count());
		assertEquals("Contenu", post.paragraphs.get().content);
	}
	
	@Test
	public void reply()
	{
		Thread thread = Thread.create(julien, "Titre2", "Contenu");
		Post post = thread.rootPost;
		post.get();

		Paragraph paragraph = post.paragraphs.get();
		paragraph.reply(julien, "Reply");
		
		Post reply = Post.all().filter("parent", paragraph).get();
		assertEquals(2, Post.all().count());
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
	public void threads()
	{
		Thread thread = Thread.create(julien, "Titre3", "Contenu");

		thread = Thread.all().filter("title", "Titre3").get();
		assertNotNull(thread);
		assertNotNull(thread.rootPost);
		thread.rootPost.get();
		assertEquals("Contenu", thread.rootPost.content);
	}
	
	@Test
	public void follow()
	{
		User chris = new User("Christopher", "redeye4@gmail.com", "Europe/Paris", false);
		chris.insert();
		
		Thread thread = Thread.create(julien, "Titre4", "Contenu");
		
		Following f = Following.all().get();
		f.thread.get();
		//f.user.get();
		//System.out.println(f.id + " " + f.thread + " " + f.user);
		assertEquals(1, julien.followedThreads.count()); // Thread is automatically followed by its author
		assertTrue(julien.followedThreads.filter("thread", thread).get() != null);
		assertEquals(0, chris.followedThreads.count());
		
		chris.follow(thread);
		
		assertEquals(1, chris.followedThreads.count());
		assertTrue(chris.followedThreads.filter("thread", thread).get() != null);
	}
	
	@Test
	public void sienaFails()
	{
		A a = new A();
		a.insert();

		C c = new C();
		c.insert();
		
		a.addC(c);
	}
}
