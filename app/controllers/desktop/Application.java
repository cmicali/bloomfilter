package controllers.desktop;

import controllers.core.DesktopControllerBase;
import core.remoteapis.facebook.FacebookApi;
import core.remoteapis.twitter.TwitterApi;
import core.remoteapis.twitter.TwitterUser;
import core.validators.NewUsernameCheck;
import models.User;
import play.data.validation.CheckWith;
import play.libs.Crypto;


public class Application extends DesktopControllerBase {

    public static void index() {
        render("desktop/Home/index.html");
    }

	public static void logout() {
        clearSession();
		redirect("/");
	}

    public static void facebookLogin() {
        attemptFacebookLogin();
        redirect("/");
    }

    public static void twitterLogin() {
        if (attemptTwitterLogin()) {
        }
        else {
            redirect("/");
        }
    }

    public static void finishTwitterLogin(String oauth_token, String oauth_verifier) {
        handleFinishTwitterLogin(oauth_token, oauth_verifier);
        redirect("/");
    }

    public static void register() {
        render("Secure/register.html");
    }

    public static void registerSocial() {
        if (session.contains(TwitterApi.SESSION_USER)) {
            TwitterUser twuser = new TwitterUser(session.get(TwitterApi.SESSION_USER));
            render("Secure/registersocial.html", twuser);
        }
        else if (session.contains(FacebookApi.KEY_FACEBOOK_USER_ID)) {
            String fbusername = session.get(FacebookApi.KEY_FACEBOOK_USER_NAME);
            String fbuserid = session.get(FacebookApi.KEY_FACEBOOK_USER_ID);
            String fbname = session.get(FacebookApi.KEY_FACEBOOK_FULLNAME);
            render("Secure/registersocial.html", fbusername, fbuserid, fbname);
        }
        else {
            redirect("/");
        }
    }

    public static void registerUser(@CheckWith(NewUsernameCheck.class) String username, String password) {
        validation.required(username);
        validation.required(password);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            register();
        }
        models.User u = new User();
        u.name = username;
        u.username = username;
        u.password = Crypto.passwordHash(password);
        u.save();
        loginUser(u);
        redirect("/");
    }

    public static void completeSocialRegistration(@CheckWith(NewUsernameCheck.class) String username) {
        validation.required(username);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            registerSocial();
        }
        if (session.contains(TwitterApi.SESSION_USER)) {
            TwitterUser twuser = new TwitterUser(session.get(TwitterApi.SESSION_USER));
            User u = new User(twuser);
            u.save();
            loginUser(u);            
        }
        else if (session.contains(FacebookApi.KEY_FACEBOOK_USER_ID)) {
            User u = FacebookApi.login();
            u.save();
            loginUser(u);
        }
        redirect("/");
    }

}