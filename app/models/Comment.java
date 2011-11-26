package models;

import com.petebevin.markdown.MarkdownProcessor;
import core.util.StringUtils;
import models.core.TimestampedModelBase;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * Author: chrismicali
 */
@Entity
public class Comment extends TimestampedModelBase {

    protected String comment_html;
    public String comment;

    @ManyToOne
    public User author;

    @ManyToOne
    public Post post;

    public Comment(String comment_markdown) {
        setComment(comment_markdown);
    }

    public void setComment(String comment) {
        this.comment = comment;
        MarkdownProcessor mp = new MarkdownProcessor();
        comment_html = mp.markdown(comment);
    }

    @Transient
    public String getFriendlyPostDate() {
        return StringUtils.getFriendlyTimeSinceEvent(date_modified.getTime());
    }

}
