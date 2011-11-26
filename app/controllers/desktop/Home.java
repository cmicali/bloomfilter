package controllers.desktop;

import controllers.core.DesktopControllerBase;
import models.Post;

import java.util.List;

/**
 * Author: chrismicali
 */
public class Home extends DesktopControllerBase {

    public static void index(Integer page) {
        List<Post> posts = Post.find("ORDER BY date_created DESC").fetch();
        render("desktop/Home/index.html", posts);
    }

    public static void login() {
        render("desktop/Home/login.html");
    }

    public static void viewUser(Long id) {
        render("desktop/Home/viewUser.html");
    }


}