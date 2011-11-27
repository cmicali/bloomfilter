package controllers.api;

import com.google.gson.JsonObject;
import controllers.core.ApiBase;
import core.util.StringUtils;
import core.validators.CommonsUrlCheck;
import core.validators.NewUsernameCheck;
import models.Post;

/**
 * Author: chrismicali
 */
public class UsersApi extends ApiBase {

    public static void validateUsername(String username) {

        validation.required(username);

        if (!validate()) return;

        boolean isValid = true;
        String message = "Username <strong>" + username + "</strong>" + " is available";

        NewUsernameCheck check = new NewUsernameCheck();
        isValid = check.isSatisfied(username, username);
        if (!isValid) {
            message = check.getCheckWithCheck().getMessage();
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("valid", isValid);
        jo.addProperty("username", username);
        if (!StringUtils.isEmpty(message)) {
            jo.addProperty("message", message);
        }
        endRequest(jo);
    }

}
