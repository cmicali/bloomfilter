package models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.remoteapis.twitter.TwitterUser;
import models.core.ModelBase;
import models.core.SerializableToJson;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Author: chrismicali
 */
@Entity
@Table(name = "PUser")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends ModelBase implements Serializable {

    @Column(nullable = false)
    public String name;
    @Column(nullable = true)
    public String first_name;
    @Column(nullable = true)
    public String last_name;

    @Column(nullable = true)
    public String profile_image_url;
    @Column(nullable = true)
    public String username;

    @Column(nullable = true)
    public String facebook_id;
    @Column(nullable = true)
    public String facebook_username;
    @Column(nullable = true)
    public String facebook_auth_token;

    @Column(nullable = true)
    public Long twitter_userid;
    @Column(nullable = true)
    public String twitter_screenname;
    @Column(nullable = true)
    public String twitter_token;
    @Column(nullable = true)
    public String twitter_secret;

    @Column(nullable = true)
    public String email;
    @Column(nullable = true)
    public String gender;
    @Column(nullable = true)
    public String locale;

    // TODO: Make this dynamic
    public String device_type = "iphone";

    @Column(nullable = true)
    public String apns_push_token;

    @Transient
    public Boolean is_new;

    public User(String name, String first_name, String last_name, String email, String gender, String locale) {
        this.name = name;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.gender = gender;
        this.locale = locale;
    }

    public User(com.restfb.types.User fbUser) {
        this.name = fbUser.getName();
        this.first_name = fbUser.getFirstName();
        this.last_name = fbUser.getLastName();
        this.facebook_id = fbUser.getId();
        this.facebook_username = fbUser.getUsername();
        this.username = fbUser.getUsername();
        this.profile_image_url = "http://graph.facebook.com/" + facebook_username + "/picture?type=square";
        this.email = fbUser.getEmail();
        this.gender = fbUser.getGender();
        this.locale = fbUser.getLocale();
    }

    public User(TwitterUser twUser) {
        this.name = twUser.name;
        this.twitter_userid = twUser.user_id;
        this.twitter_screenname = twUser.screen_name;
        this.twitter_token = twUser.token;
        this.twitter_secret = twUser.secret;
        this.first_name = twUser.first_name;
        this.last_name = twUser.last_name;
        this.username = twitter_screenname;
        this.profile_image_url = "https://api.twitter.com/1/users/profile_image?screen_name=" + twitter_screenname + "&size=bigger";
    }
        
    public User(User u) {
        this.name = u.name;
        this.first_name = u.first_name;
        this.last_name = u.last_name;
        this.facebook_id = u.facebook_id;
        this.facebook_username = u.facebook_username;
        this.email = u.email;
        this.gender = u.gender;
        this.locale = u.locale;
    }

    public JsonObject toJson(SerializableToJson.JsonResponseType type) {
        JsonObject jo = super.toJson(type);
        jo.addProperty("first_name", first_name);
        jo.addProperty("last_name", last_name);
        // jo.addProperty("picture", pictureUrl);
        jo.addProperty("facebook_id", facebook_id);
        jo.addProperty("facebook_username", facebook_username);
        if (type == SerializableToJson.JsonResponseType.Long) {
            jo.addProperty("email", email);
            jo.addProperty("gender", gender);
            JsonArray favs = new JsonArray();
            jo.add("num_favorites", favs);
        }
        if (is_new != null) {
            jo.addProperty("is_new", is_new.booleanValue());
        }
        return jo;
    }
/*
    public void sendPushNotification(String message, String actionKey) {
        NotificationSender.getNotificationSenderForUser(this).sendPushNotification(message, actionKey);
    }
*/
    //////////////////////////////////////////////////////////////////////////////////////////
    //// Static methods

    public static User findByFacebookId(String facebookId) {
        List<User> users = User.find("facebook_id", facebookId).fetch();
        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    public static User findByTwitterUserId(Long twitter_userid) {
        List<User> users = User.find("twitter_userid", twitter_userid).fetch();
        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    public static User findOrCreateByFacebookId(String facebook_id, User newUser) {
        User u = User.findByFacebookId(facebook_id);
        if (u == null) {
            u = newUser;
            u.save();
        }
        return u;
    }

    public static User findOrCreateByTwitterUserId(Long twitter_userid, User newUser) {
        User u = User.findByTwitterUserId(twitter_userid);
        if (u == null) {
            u = newUser;
            u.save();
        }
        return u;
    }



}
