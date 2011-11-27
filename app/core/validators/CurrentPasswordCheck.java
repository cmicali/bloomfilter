package core.validators;

import controllers.core.DesktopControllerBase;
import controllers.core.WebControllerBase;
import models.User;
import play.data.validation.Check;
import play.libs.Crypto;

/**
 * Author: chrismicali
 */
public class CurrentPasswordCheck extends Check {

    @Override
    public boolean isSatisfied(Object validatedObject, Object value) {
        if (value == null || value.toString().length() == 0) {
            setMessage("Required");
            return false;
        }
        String pw = value.toString();
        boolean isSatisfied = User.find("id = ? AND password = ?", WebControllerBase.getUserId(), Crypto.passwordHash(pw)).fetch().size() > 0;
        if (!isSatisfied) {
            setMessage("Does not match current password");
        }
        return isSatisfied;
    }
}