package core.remoteapis.twitter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import core.util.StringUtils;
import play.libs.WS;

import java.io.Serializable;

/**
 * Author: chrismicali
 */
public class TwitterUser implements Serializable {

    public String token;
    public String secret;
    
    public String screen_name;
    public Long user_id;
    public String name;
    public String first_name;
    public String last_name;
    
    public TwitterUser(String json) {
        this(StringUtils.parseJson(json));
    }

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
    
    public String getPictureUrl() {
        return "https://api.twitter.com/1/users/profile_image?screen_name=" + screen_name + "&size=bigger";
    }
    
    @Override
    public String toString() {
        JsonObject jo = new JsonObject();
        jo.addProperty("screen_name", screen_name);
        jo.addProperty("id", user_id);
        jo.addProperty("name", name );
        jo.addProperty("token", token);
        jo.addProperty("secret", secret);
        return jo.toString();
    }
    
}
