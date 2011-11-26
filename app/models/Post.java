package models;

import core.util.StringUtils;
import models.core.CreatedTimestampModelBase;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Author: chrismicali
 */
@Entity
public class Post extends CreatedTimestampModelBase {

    public String link;
    public String title;

    @ManyToOne
    public User author;

    @OneToMany(mappedBy = "post")
    public List<Comment> comments;
    
    @Transient
    public String getPermaLink() {
        return "/posts/" + id + "_" + StringUtils.cleanStringForPermalink(title);
    }

    @Transient
    public String getFriendlyPostDate() {
        return StringUtils.getFriendlyTimeSinceEvent(date_created.getTime());
    }

    @Transient
    public String getDomain() {
        try {
            URL u = new URL(link);
            return u.getHost();
        }
        catch (Exception ex) {}
        return "";
    }

    @Transient
    public int getNumComments() {
        return comments.size();
    }

    public static boolean isLinkAlreadyPosted(String url) {
        Date d = new Date();
        d.setTime(System.currentTimeMillis() - (1000*60*60)*24 * 2);
        return Post.find("link = ? AND date_created >= ?", url, d).fetch().size() > 0;
    }

}
