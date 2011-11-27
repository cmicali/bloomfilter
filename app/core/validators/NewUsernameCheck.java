package core.validators;

import models.User;
import org.apache.commons.validator.UrlValidator;
import play.data.validation.Check;
import play.data.validation.CheckWithCheck;

/**
 * Author: chrismicali
 */
@SuppressWarnings("serial")
public class NewUsernameCheck extends Check {

    @Override
    public void setMessage(String message, String... vars) {
        if (checkWithCheck == null) { checkWithCheck = new CheckWithCheck(); }
        super.setMessage(message, vars);
    }

    @Override
    public boolean isSatisfied(Object validatedObject, Object value) {
        if (value == null || value.toString().length() == 0) {
            setMessage("Required");
            return false;
        }
        String username = value.toString();
        if (username.length() < 2) {            
            setMessage("Username must be at least 2 characters");
            return false;
        }
        if (User.find("username", username).fetch().size() > 0) {
            setMessage("Username is already taken");
            return false;
        }
        return true;
    }

}
