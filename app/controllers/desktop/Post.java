package controllers.desktop;

import controllers.Secure;
import controllers.core.DesktopControllerBase;
import core.util.StringUtils;
import core.validators.CommonsUrlCheck;
import models.Comment;
import play.data.validation.CheckWith;
import play.mvc.With;

/**
 * Author: chrismicali
 */
@With(Secure.class)
public class Post extends DesktopControllerBase {

    public static void viewPost(Long id) {
        models.Post post = models.Post.findById(id);
        render("desktop/Home/viewPost.html", post);
    }

    public static void newPost() {
        render("desktop/Home/newPost.html");
    }

    public static void submitNewComment(Long postId, String comment) {
        validation.required(comment);
        validation.required(postId);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        }
        else {
            // Save comment
            Comment c = new Comment(comment);
            c.author = getUser();
            c.post = models.Post.findById(postId);
            c.save();
        }
        viewPost(postId);
    }

    public static void submitNewPost(String title, @CheckWith(CommonsUrlCheck.class) String url, String comment) {
        validation.required(title);
        validation.required(url);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newPost();
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = "http://" + url;
        }
        models.Post p = new models.Post();
        p.author = getUser();
        p.link = url;
        p.title = title;
        p.save();
        if (StringUtils.isNotEmpty(comment)) {
            Comment c = new Comment(comment);
            c.author = getUser();
            c.post = p;
            c.save();
        }

        Home.index(0);
    }
}
