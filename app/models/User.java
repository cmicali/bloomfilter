package models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.remoteapis.twitter.TwitterUser;
import core.util.StringUtils;
import models.core.CreatedTimestampModelBase;
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
public class User extends CreatedTimestampModelBase implements Serializable {

    @Column(nullable = false)
    public String username;
    @Column(nullable = true)
    public String password;
    @Column(nullable = false)
    public String name;
    @Column(nullable = true)
    public String profile_image_url;

    @Column(nullable = true)
    public String facebook_id;
    @Column(nullable = true)
    public String facebook_username;
    @Column(nullable = true)
    public String facebook_auth_token;
    @Column(nullable = true)
    public String facebook_name;

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

    @Transient
    public Boolean is_new;

    @Transient
    public boolean isTwitterConnceted() {
        return twitter_userid != null && twitter_userid > 0;
    }

    @Transient
    public boolean isFacebookConnected() {
        return StringUtils.isNotEmpty(facebook_id);
    }

    @Transient
    public String getMemberSince() {
        return StringUtils.getFriendlyElapsedTimeString(date_created);
//        DateFormat fmt = new SimpleDateFormat("MMMM d, yyyy");
//        return fmt.format(date_created);
    }

    public User() {
    }

    public User(String name, String email, String gender, String locale) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.locale = locale;
    }

    public User(com.restfb.types.User fbUser) {        
        updateFacebookDetails(fbUser);
        this.name = fbUser.getName();
        this.username = fbUser.getUsername();
        this.email = fbUser.getEmail();
        this.gender = fbUser.getGender();
        this.locale = fbUser.getLocale();
    }

    public User(TwitterUser twUser) {
        updateTwitterDetails(twUser);
        this.name = twUser.name;
        this.username = twitter_screenname;
    }
        
    public User(User u) {
        this.name = u.name;
        this.facebook_id = u.facebook_id;
        this.facebook_username = u.facebook_username;
        this.email = u.email;
        this.gender = u.gender;
        this.locale = u.locale;
    }

    public void updateTwitterDetails(TwitterUser twUser) {
        this.twitter_userid = twUser.user_id;
        this.twitter_screenname = twUser.screen_name;
        this.twitter_token = twUser.token;
        this.twitter_secret = twUser.secret;
        if (StringUtils.isEmpty(profile_image_url)) {
            this.profile_image_url = twUser.getPictureUrl();
        }
    }
    
    public void updateFacebookDetails(com.restfb.types.User fbUser) {
        this.facebook_id = fbUser.getId();
        this.facebook_username = fbUser.getUsername();
        this.facebook_name = fbUser.getName();
        if (StringUtils.isEmpty(profile_image_url)) {
            this.profile_image_url = "http://graph.facebook.com/" + facebook_username + "/picture?type=square";            
        }
    }
    
    public JsonObject toJson(SerializableToJson.JsonResponseType type) {
        JsonObject jo = super.toJson(type);
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
