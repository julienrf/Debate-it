package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import models.Following;
import models.Room;
import models.Thread;
import models.User;
import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.modules.gae.GAE;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Router;
import utils.Pagination;

/**
 * This controller defines basic actions such as login, logout or edit profile
 * @author julien
 *
 */
public class Dbtit extends Controller {
	
	@Before(priority=10)
	protected static void checkLoggedIn() {
		LoggedIn loggedIn = getActionAnnotation(LoggedIn.class);
		if (loggedIn != null) {
			if (connectedUser() == null) {
				login(request.method == "GET" ? request.url : Router.getFullUrl("Dbtit.index"));
			}
		}
	}
	
	@Before(priority=20)
	protected static void putUser() {
		renderArgs.put("user", connectedUser());
	}
	
	protected static User connectedUser() {
		com.google.appengine.api.users.User gaeUser = GAE.getUser();
		if (gaeUser != null) {
			return User.findByEmail(gaeUser.getEmail());
		} else {
			return null;
		}
	}

	public static void login(String url) {
		if (url != null)
			flash.put("url", url);
		GAE.login("Dbtit.loggedIn");
	}
	
	public static void loggedIn() {
		String url = flash.contains("url") ? flash.get("url") : Router.getFullUrl("Dbtit.index");
		com.google.appengine.api.users.User gaeUser = GAE.getUser();
		if (gaeUser != null) {
			User user = User.findByEmail(gaeUser.getEmail());
			if (user == null) {
				user = User.create(gaeUser.getNickname(), gaeUser.getEmail(), TimeZone.getDefault().getID());
				Logger.info("New user %s (%s) created", user.email, user.id);
				flash.success(Messages.get("welcomeFirst"));
				profile(url);
			}
		} else {
			flash.error(Messages.get("loginFail"));
		}
		redirect(url);
	}
	
	public static void logout() {
		GAE.logout("Dbtit.index");
	}
	
	@LoggedIn
	public static void profile(String url) {
		User.Update userUpdate = new models.User.Update(connectedUser());
		if (url != null)
			flash.put("url", url);
		render(userUpdate);
	}
	
	@LoggedIn
	public static void postProfile(@Valid User.Update userUpdate) {
		User connectedUser = connectedUser();
		if (validation.hasErrors()) {
			render("@profile", userUpdate);
		}
		connectedUser.updateProfile(userUpdate);
		Logger.info("Profile updated by %s (%s)", connectedUser.name, connectedUser.id);
		flash.success(Messages.get("profileUpdated"));
		String url = flash.get("url");
		if (url == null)
			url = Router.reverse("Dbtit.index").url;
		redirect(url);
	}
	
	/**
	 * Display the home page
	 */
    public static void index() {
    	Room room = Room.getOpenRoom();
    	List<Thread> threads = Thread.sortByLastPost(room.threads.fetch());
    	
    	Pagination pagination = new Pagination(room.threads.count());
    	int currentPage = 1;
    	if (params._contains(pagination.getPageVar()))
    		currentPage = params.get(pagination.getPageVar(), Integer.class);
    	pagination.setCurrentPage(currentPage);
    	
    	
    	if (pagination.getCurrentLimit() > 0) {
    		threads = threads.subList(pagination.getCurrentOffset(), pagination.getCurrentLimit());
    	} else {
    		threads = new ArrayList<Thread>();
    	}
    	
    	render(threads, pagination);
    }
    
    public static void features() {
    	render();
    }
}