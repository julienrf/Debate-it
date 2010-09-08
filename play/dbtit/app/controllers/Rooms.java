package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Room;
import models.RoomSubscription;
import models.Thread;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import utils.Pagination;

@With(Dbtit.class)
public class Rooms extends Controller {
	
	@LoggedIn
	public static void list() {
		User user = Dbtit.connectedUser();
		List<RoomSubscription> subscriptions;
		Pagination pagination = new Pagination(user.subscriptions.count());
		
    	int currentPage = 1;
    	if (params._contains(pagination.getPageVar()))
    		currentPage = params.get(pagination.getPageVar(), Integer.class);
    	pagination.setCurrentPage(currentPage);
    	
    	
    	if (pagination.getCurrentLimit() > 0) {
    		subscriptions = user.subscriptions.fetch(pagination.getCurrentLimit(), pagination.getCurrentOffset());
    	} else {
    		subscriptions = new ArrayList<RoomSubscription>();
    	}
		
		render(subscriptions, pagination);
	}
	
	/**
	 * Show the debates of the room
	 * @param hash
	 */
	public static void show(String hash) {
		Room room = Room.findByHash(hash);
		notFoundIfNull(room);
		
		/*if (!(hash.equals("open") || room.isPublic)) {
			User user = Dbtit.connectedUser();
			if (user == null || !user.hasSubscribed(room)) {
				forbidden();
			}
		}*/
		
		List<Thread> threads = Thread.sortByLastPost(room.threads.fetch());
		Pagination pagination = new Pagination(room.threads.count());
		int currentPage = 1;
		if (params._contains(pagination.getPageVar())) {
			currentPage = params.get(pagination.getPageVar(), Integer.class);
		}
		pagination.setCurrentPage(currentPage);
		
		if (pagination.getCurrentLimit() > 0) {
			threads = threads.subList(pagination.getCurrentOffset(), pagination.getCurrentLimit());
		}
		
		render(room, threads, pagination);
	}
	
	@LoggedIn
	public static void newRoom() {
		render();
	}
	
	/**
	 * Creates a new debate room
	 * @param name
	 * @param isPublic
	 */
	@LoggedIn
	public static void create(@Required String name, @Required boolean isPublic) {
		if (validation.hasErrors()) {
			render("@newRoom", name, isPublic);
		}
		
		Room room = Room.create(Dbtit.connectedUser(), name/*, isPublic*/);
		show(room.hash);
	}
}
