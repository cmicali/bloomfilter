package controllers.desktop;

import controllers.core.DesktopControllerBase;


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
}