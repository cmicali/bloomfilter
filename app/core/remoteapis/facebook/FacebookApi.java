package core.remoteapis.facebook;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import controllers.core.WebControllerBase;
import core.util.StringUtils;
import models.User;
import play.Play;
import play.libs.OAuth2;
import play.mvc.Http;
import play.mvc.Scope;

/**
 * Author: chrismicali
 */
public class FacebookApi {

    public static String KEY_FACEBOOK_USER_ID = "facebook_id";
    public static String KEY_FACEBOOK_USER_NAME = "facebook_username";
    public static String KEY_FACEBOOK_FULLNAME = "facebook_fullname";
    public static String KEY_FACEBOOK_ACCESS_TOKEN = "facebook_access_token";

    private static OAuth2 FACEBOOK = new OAuth2(
            "https://graph.facebook.com/oauth/authorize",
            "https://graph.facebook.com/oauth/access_token",
            FacebookApi.getAPIKey(),
            FacebookApi.getSecret()
    );

    public static FacebookClient getClient() {
        return new DefaultFacebookClient(getAuthToken());
    }

    public static FacebookClient getClient(String accessToken) {
        return new DefaultFacebookClient(accessToken);
    }
    
    public static String getAPIKey() {
        return Play.configuration.getProperty("facebook.appId");
    }

    public static String getSecret() {
        return Play.configuration.getProperty("facebook.appSecret");
    }

    public static User login() {
        try {
            String userId = FacebookApi.getUserId();
            String authToken = getAuthToken();
            User u = User.findByFacebookId(userId);
            if (u == null) {
                String userid = Scope.Session.current().get(WebControllerBase.KEY_USER_ID);
                com.restfb.types.User fbUser = FacebookApi.getClient().fetchObject("me", com.restfb.types.User.class);
                if (StringUtils.isNotEmpty(userid)) {
                    u = User.findById(Long.parseLong(userid));
                    u.updateFacebookDetails(fbUser);
                    u.save();
                }
                else {
                    u = new User(fbUser);
                }
            }
            else {
                if (!authToken.equals(u.facebook_auth_token)) {
                    u.facebook_auth_token = authToken;
                    u.save();
                }                
            }
            Scope.Session.current().put(KEY_FACEBOOK_ACCESS_TOKEN, u.facebook_auth_token);
            Scope.Session.current().put(KEY_FACEBOOK_USER_ID, u.facebook_id);
            Scope.Session.current().put(KEY_FACEBOOK_USER_NAME, u.facebook_username);
            Scope.Session.current().put(KEY_FACEBOOK_FULLNAME, u.name);
            return u;
        }
        catch(Exception ex) {
            logout();
        }
        return null;
    }


    public static void logout() {
        Scope.Session.current().remove(KEY_FACEBOOK_ACCESS_TOKEN);
        Scope.Session.current().remove(KEY_FACEBOOK_USER_ID);
        Scope.Session.current().remove(KEY_FACEBOOK_USER_NAME);
        Scope.Session.current().remove(KEY_FACEBOOK_FULLNAME);
    }

    public static String getUserId() {
        if (Scope.Session.current().contains(KEY_FACEBOOK_USER_ID)) {
            return Scope.Session.current().get(KEY_FACEBOOK_USER_ID);
        }
        else {
            JsonObject jo = getSignedRequest();
            String uid = jo.get("user_id").getAsString();
            Scope.Session.current().put(KEY_FACEBOOK_USER_ID, uid);
            return uid;
        }
    }

    public static String getAuthToken() {
        if (Scope.Session.current().contains(KEY_FACEBOOK_ACCESS_TOKEN)) {
            return Scope.Session.current().get(KEY_FACEBOOK_ACCESS_TOKEN);
        }
        else {
            JsonObject jo = getSignedRequest();
            String code = jo.getAsJsonObject().get("code").getAsString();
            Scope.Params.current().put("code", code);
            OAuth2.Response response = FacebookApi.FACEBOOK.retrieveAccessToken("");
            Scope.Session.current().put(KEY_FACEBOOK_ACCESS_TOKEN, response.accessToken);
            return response.accessToken;
        }
    }

    private static JsonObject getSignedRequest() {
        Http.Cookie c = Http.Request.current().cookies.get("fbsr_" + getAPIKey());
        // TODO: Verify SHA1
        JsonElement jo = StringUtils.base64UrlDecodeToJson(c.value.split("\\.")[1]);
        return jo.getAsJsonObject();
    }

}
