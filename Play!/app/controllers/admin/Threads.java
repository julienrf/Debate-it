package controllers.admin;

import controllers.CRUD;
import controllers.Secure;
import play.mvc.With;

@With(Secure.class)
public class Threads extends CRUD {

}
