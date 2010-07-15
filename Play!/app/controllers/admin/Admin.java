package controllers.admin;

import controllers.Secure;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends Controller {
	public static void index() {
		render("@CRUD.index");
	}
}
