package controllers.desktop;

import controllers.Secure;
import controllers.core.DesktopControllerBase;
import core.validators.CurrentPasswordCheck;
import models.*;
import play.data.validation.CheckWith;
import play.libs.Crypto;

/**
 * Author: chrismicali
 */
public class User extends DesktopControllerBase {
    
    public static void viewUser(Long id) {
        models.User user = models.User.findById(id);
        long numPosts = models.Post.count("author = ?", user);
        long numComments = Comment.count("author = ?", user);
        flash.put("name", user.name);
        flash.put("pictureurl", user.profile_image_url);
        render("desktop/Home/viewUser.html", user, numPosts, numComments);
    }
    
    public static void updatePassword(@CheckWith(CurrentPasswordCheck.class) String password, String newPassword, String passwordConfirm) {
        models.User u = models.User.findById(getUserId());
        validation.required(password);
        validation.required(newPassword);
        validation.required(passwordConfirm);
        validation.equals(newPassword, passwordConfirm);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        }
        else {
            u.password = Crypto.passwordHash(newPassword);
            u.save();
            flash.put("pwmessage", "Password changed");
        }
        viewUser(u.id);
    }
    
    public static void updateDetails(String name, String pictureurl) {
        models.User u = models.User.findById(getUserId());
        validation.required(name);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        }
        else {
            u.name = name;
            u.profile_image_url = pictureurl;
            u.save();
            flash.put("detailsmessage", "Details saved");
        }
        viewUser(u.id);
    }
    
}
