package models;

import core.util.StringUtils;
import models.core.CreatedTimestampModelBase;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.net.URL;
import java.util.ArrayList;
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
    public long getNumComments() {
        if (numComments != null) 
            return numComments;
        else
            return comments.size();
    }
    
    @Transient
    public Long numComments = null;

    public static boolean isLinkAlreadyPosted(String url) {
        Date d = new Date();
        d.setTime(System.currentTimeMillis() - (1000*60*60)*24 * 2);
        return Post.find("link = ? AND date_created >= ?", url, d).fetch().size() > 0;
    }

    public static List<Post> getPostList(int startIndex, int pageSize) {
        List results = Post.find("SELECT p, count(c) FROM Post p JOIN FETCH p.author a LEFT JOIN p.comments as c GROUP BY p.id, a.id ORDER BY p.date_created DESC").from(startIndex).fetch(pageSize);
        List<Post> posts = new ArrayList<Post>();
        for(Object o : results) {
            Object[] row = (Object[])o;
            Post p = (Post)row[0];
            posts.add(p);
            p.numComments = (Long)row[1];
        }
        return posts;
    }

}
