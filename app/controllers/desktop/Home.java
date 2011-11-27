package controllers.desktop;

import controllers.core.DesktopControllerBase;
import models.Comment;
import models.Post;
import models.User;

import java.util.List;

/**
 * Author: chrismicali
 */
public class Home extends DesktopControllerBase {

    public static int PAGE_SIZE = 25;
    
    public static void index(Integer start) {
        if (start == null) start = 0;
        List<Post> posts = Post.getPostList(start, PAGE_SIZE);
        int next_page = start + PAGE_SIZE;
        int prev_page = start - PAGE_SIZE;
        if (prev_page < 0) prev_page = -1;
        if (next_page >= Post.count()) next_page = -1;
        render("desktop/Home/index.html", posts, next_page, prev_page);
    }

    public static void about() {
        render("desktop/Home/about.html");
    }

    public static void bookmarklet() {
        render("desktop/Home/bookmarklet.html");
    }

    public static void viewPost(Long id) {
        Post post = Post.find("SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.comments WHERE p.id = ? ", id).first();
        render("desktop/Home/viewPost.html", post);
    }
}