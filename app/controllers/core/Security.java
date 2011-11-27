package controllers.core;

import controllers.Secure;
import core.util.StringUtils;
import models.User;
import play.libs.Crypto;

/**
 * Author: chrismicali
 */
public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return false;
        }
        String hash = Crypto.passwordHash(password);
        User user = User.find("username = ? AND password = ?", username, hash).first();
        if (user != null) {
            DesktopControllerBase.loginUser(user);
            return true;
        }
        else {
            return false;
        }
    }

}
