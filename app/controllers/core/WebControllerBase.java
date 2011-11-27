package controllers.core;

import controllers.desktop.Application;
import core.remoteapis.facebook.FacebookApi;
import core.remoteapis.twitter.TwitterApi;
import core.util.StringUtils;
import core.util.UAgentInfo;
import models.User;
import play.cache.Cache;
import play.mvc.Scope;

/**
 * Author: chrismicali
 */
public class WebControllerBase extends ControllerBase {

    protected static final String KEY_USER = "user";
    public static final String KEY_USER_ID = "userid";
    protected static final String KEY_USER_NAME = "username";
    protected static final String KEY_USER_DISPLAY_NAME = "userdisplayname";
    protected static final String KEY_USER_PIC = "userpic";

    protected static UAgentInfo getUserAgent() {
        String useragent = "";
        String accept = "";
        if (request.headers.containsKey("user-agent")) {
            useragent = request.headers.get("user-agent").value();
        }
        if (request.headers.containsKey("accept")) {
            accept = request.headers.get("accept").value();
        }
        UAgentInfo ua = new UAgentInfo(useragent, accept);
        return ua;
    }

    public static Long getUserId() {
        if (session.contains(KEY_USER_ID)) {
            return Long.parseLong(session.get(KEY_USER_ID));
        }
        else {
            return -1l;
        }
    }

    protected static User getUser() {
        User u = Cache.get(KEY_USER, User.class);
        if (u == null) {
            u = User.findById(getUserId());
            Cache.set(KEY_USER, u);
        }
        return u;
    }

    protected static void clearSession() {
        Scope.Session.current().remove(KEY_USER_ID);
        Scope.Session.current().remove(KEY_USER_NAME);
        Scope.Session.current().remove(KEY_USER_PIC);
        Scope.Session.current().clear();
        Cache.safeDelete(KEY_USER);
    }

    protected static void loginUser(User u) {
        Scope.Session.current().put(KEY_USER_ID, u.id);
        Scope.Session.current().put(KEY_USER_NAME, u.username);
        Scope.Session.current().put(KEY_USER_DISPLAY_NAME, u.name);
        Scope.Session.current().put(KEY_USER_PIC, u.profile_image_url);
    }

    protected static boolean attemptFacebookLogin() {
        User u = FacebookApi.login();
        if (u != null) {
            if (u.id == null) {
                Application.registerSocial();
            }
            else {
                loginUser(u);
            }
            return true;
        }
        else {
            FacebookApi.logout();
            return false;
        }

    }
    
    protected static boolean attemptTwitterLogin() {
        String authUrl = TwitterApi.getAuthorizeUrl();
        if (StringUtils.isNotEmpty(authUrl)) {
            redirect(authUrl);
            return true;
        }
        return false;
    }

    protected static boolean handleFinishTwitterLogin(String oauth_token, String oauth_verifier) {
        User u = TwitterApi.finishTwitterLogin(oauth_token, oauth_verifier);
        if (u != null) {
            loginUser(u);
            return true;
        }
        else {
            // User is a new user
            Application.registerSocial();
            return false;
        }
    }
    
    
}
