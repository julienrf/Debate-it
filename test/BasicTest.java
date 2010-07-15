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
		
		julien = new User("Julien Richard-Foy", "julien.rf@no-log.org", TimeZone.getDefault(), false);
		julien.save();
	}
	
	@Test
	public void user()
	{
		User user = User.find("byEmail", "julien.rf@no-log.org").first();
		assertNotNull(user);
		assertEquals("Julien Richard-Foy", user.name);
	}
	
	@Test
	public void post()
	{
		Post post = Post.create(julien, "Contenu", null, null);

		post = Post.find("byParentIsNull").first();
		assertNotNull(post);
		assertEquals("Contenu", post.content);
		assertEquals(post.paragraphs.size(), 1);
		assertEquals(post.paragraphs.get(0).content, "Contenu");
	}
	
	@Test
	public void reply()
	{
		Post post = Post.create(julien, "Contenu", null, null);

		Paragraph paragraph = post.paragraphs.get(0);
		paragraph.reply(julien, "Reply");
		
		Post reply = Post.find("byParent", paragraph).first();
		assertNotNull(reply);
		assertEquals("Reply", reply.content);
	}
	
	@Test
	public void edit()
	{
		Post post = Post.create(julien, "Contenu", null, null);

		post.edit("Nouveau contenu");
		
		assertEquals("Nouveau contenu", post.content);
		assertEquals(post.paragraphs.size(), 1);
		
		post.edit("Nouveau\n\ncontenu");
		
		assertEquals(post.paragraphs.size(), 2);
	}
	
	@Test
	public void threads()
	{
		Thread thread = Thread.create(julien, "Titre", "Contenu");

		thread = Thread.find("byTitle", "Titre").first();
		assertNotNull(thread);
		assertNotNull(thread.rootPost);
		assertEquals(thread.rootPost.content, "Contenu");
	}
}
