package controllers.core;

import controllers.Secure;
import models.User;

/**
 * Author: chrismicali
 */
public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
        User user = User.find("byEmail", username).first();
        return false;
    }

}
