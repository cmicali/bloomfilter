package controllers.api;

import com.google.gson.JsonObject;
import controllers.core.ApiBase;
import core.util.StringUtils;
import core.validators.CommonsUrlCheck;
import models.Post;
import org.apache.commons.validator.UrlValidator;
import play.data.validation.CheckWith;

import java.net.URL;

/**
 * Author: chrismicali
 */
public class PostsApi extends ApiBase {

    public static void validateUrl(String url) {

        validation.required(url);

        if (!validate()) return;

        boolean isValid = true;
        String message = "";

        CommonsUrlCheck check = new CommonsUrlCheck();
        isValid = check.isSatisfied(url, url);
        if (!isValid) {
            message = "Not a valid URL";
        }

        if (isValid && Post.isLinkAlreadyPosted(url)) {
            isValid = false;
            message = "URL has been posted recently";
        }
        
        JsonObject jo = new JsonObject();
        jo.addProperty("valid", isValid);
        jo.addProperty("url", url);
        if (!StringUtils.isEmpty(message)) {
            jo.addProperty("message", message);
        }
        endRequest(jo);
    }


}
