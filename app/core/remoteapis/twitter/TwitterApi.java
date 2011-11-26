package core.remoteapis.twitter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import core.util.Log;
import models.User;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import play.Play;
import play.cache.Cache;
import play.data.parsing.UrlEncodedParser;
import play.libs.WS;
import play.mvc.Router;


import java.util.Map;

/**
 * Author: chrismicali
 */
public class TwitterApi {

    private static String AUTHORIZE_URL = "http://twitter.com/oauth/authorize";
    private static String ACCESS_TOKEN_URL = "http://twitter.com/oauth/access_token";

    private static OAuthService service = null;

    private static OAuthService getService() {
        if (service == null) {
            String callback = getCallback();
            service = new ServiceBuilder()
                            .provider(org.scribe.builder.api.TwitterApi.class)
                            .callback(callback)
                            .apiKey(getConsumerKey())
                            .apiSecret(getConsumerSecret())
                            .build();
        }
        return service;
    }

    private static String getCallback() {
   	String action = "desktop.Application.finishTwitterLogin";
        if (Play.id.equals("test")) {
            return "http://localhost:" + Play.configuration.get("http.port")
                + Router.reverse(action).toString();
       }
        return Router.getFullUrl(action);
    }

    private static String getConsumerKey() {
        return Play.configuration.getProperty("twitter.consumerKey");
    }

    private static String getConsumerSecret() {
        return Play.configuration.getProperty("twitter.consumerSecret");
    }

    public static String getAuthorizeUrl() {
        Token requestToken = getService().getRequestToken();
       	Cache.set(requestToken.getToken(), requestToken, "60min");
       	return getService().getAuthorizationUrl(requestToken);
    }

    public static User finishTwitterLogin(String oauth_token, String oauth_verifier) {
        Verifier verifier = new Verifier(oauth_verifier);
        Token requestToken = (Token) Cache.get(oauth_token);
        Token accessToken = getService().getAccessToken(requestToken, verifier);
        Map<String, String[]> params = UrlEncodedParser.parse(accessToken.getRawResponse());

        String token = accessToken.getToken();
        String secret = accessToken.getSecret();
        String screenName = params.get("screen_name")[0];
        Long userId = Long.parseLong(params.get("user_id")[0]);
                
        User u = User.findByTwitterUserId(userId);
        if (u == null) {
            TwitterUser twUser = TwitterUser.fetch(screenName);
            twUser.token = token;
            twUser.secret = secret;
            u = new User(twUser);
            u.save();
        }

        return u;

    }

}
