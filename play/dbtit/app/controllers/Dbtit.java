package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import models.Post;
import models.Thread;
import models.User;
import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.Crypto;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Router;
import play.mvc.With;

/**
 * This controller defines basic actions such as login, logout or edit profile
 * @author julien
 *
 */
public class Dbtit extends Controller {
	
	@Before(priority=10)
	protected static void autoLogin() {
		if (connectedUser() == null) {
			// Copied from Secure module
			Http.Cookie remember = request.cookies.get("rememberme");
	        if(remember != null && remember.value.indexOf("-") > 0) {
	            String sign = remember.value.substring(0, remember.value.indexOf("-"));
	            String email = remember.value.substring(remember.value.indexOf("-") + 1);
	            if(Crypto.sign(email).equals(sign)) {
	                session.put("user-email", email);
	            }
	        }
		}
	}
	
	@Before(priority=20)
	protected static void putUser() {
		renderArgs.put("user", connectedUser());
	}
	
	@Before(priority=20)
	protected static void checkAuthenticated() throws Throwable {
		Authenticated authenticated = getActionAnnotation(Authenticated.class);
		if (authenticated != null) {
			if (connectedUser() == null) {
				login(request.method == "GET" ? request.url : "/");
			}
		}
	}
	
	protected static User connectedUser() {
		if (!session.contains("user-email")) {
			return null;
		}
		return User.find("byEmail", session.get("user-email")).first();
	}

	public static void login(String url) {
		if (!OpenID.isAuthenticationResponse()) { // L’utilisateur vient d’arriver sur la page login
			if (url != null)
				flash.put("url", url);
			if (!OpenID.id("https://www.google.com/accounts/o8/id")
					.required("email", "http://axschema.org/contact/email")
					.verify()) {
				flash.error(Messages.get("openIdError"));
				index();
			}
		} else { // L’utilisateur est redirigé, par le fournisseur OpenID, sur la page login
			UserInfo verifiedUser = OpenID.getVerifiedID();
			if (verifiedUser == null) {
				flash.error(Messages.get("loginFail"));
				index();
			}
			String email = verifiedUser.extensions.get("email");
			session.put("user-email", email);
			User user = User.find("byEmail", email).first();
			String redirectUrl = flash.get("url");
			if (redirectUrl == null)
				redirectUrl = Router.reverse("Dbtit.index").url;
			if (user == null) { // New user ?
				String name = email.substring(0, email.indexOf("@"));
				user = new User(name, email, TimeZone.getTimeZone("Europe/Paris"), false).save();
				Logger.info("New user %s (%s) created", email, user.id);
				flash.success(Messages.get("welcomeFirst"));
				profile(redirectUrl);
			} else {
				if (user.rememberMe) {
					response.setCookie("rememberme", Crypto.sign(email) + "-" + email, "365d");
				}
				flash.success(Messages.get("welcome", user.name));
				redirect(redirectUrl);
			}
		}
	}
	
	public static void logout() {
		session.remove("user-email");
		response.setCookie("rememberme", "", 0);
		index();
	}
	
	@Authenticated
	public static void profile(String url) {
		User.Update userUpdate = new models.User.Update(connectedUser());
		if (url != null)
			flash.put("url", url);
		render(userUpdate);
	}
	
	@Authenticated
	public static void postProfile(@Valid User.Update userUpdate) {
		User connectedUser = connectedUser();
		if (validation.hasErrors()) {
			render("@profile", userUpdate);
		}
		connectedUser.update(userUpdate);
		Logger.info("Profile updated by %s (%s)", connectedUser.name, connectedUser.id);
		flash.success(Messages.get("profileUpdated"));
		String url = flash.get("url");
		if (url == null)
			url = Router.reverse("Dbtit.index").url;
		redirect(url);
	}
	
	/**
	 * Display the thread list
	 */
    public static void index() {
    	render();
    }
}