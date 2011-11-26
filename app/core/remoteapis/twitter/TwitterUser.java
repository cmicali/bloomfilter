package core.remoteapis.twitter;

import com.google.gson.JsonObject;
import play.libs.WS;

/**
 * Author: chrismicali
 */
public class TwitterUser {

    public String token;
    public String secret;
    
    public String screen_name;
    public Long user_id;
    public String name;
    public String first_name;
    public String last_name;
    
    public TwitterUser(JsonObject user) {
        this.screen_name = user.get("screen_name").getAsString();
        this.user_id = user.get("id").getAsLong();
        this.name = user.get("name").getAsString();
        if (name.contains(" ")) {
            String[] parts = name.split(" ", 2);
            first_name = parts[0];
            last_name = parts[1];
        }
        else {
            first_name = name;
        }
    }

    public static TwitterUser fetch(String screen_name) {
        WS.HttpResponse res = WS.url("https://api.twitter.com/1/users/show.json?screen_name=" + screen_name).get();
        JsonObject twuser = res.getJson().getAsJsonObject();
        return new TwitterUser(twuser);
    }
}
