package core.validators;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import org.apache.commons.validator.UrlValidator;
import play.data.validation.Check;
import play.data.validation.URL;

import java.util.regex.Pattern;

/**
 * Author: chrismicali
 */
@SuppressWarnings("serial")
public class CommonsUrlCheck extends Check {

    @Override
    public boolean isSatisfied(Object validatedObject, Object value) {
        if (value == null || value.toString().length() == 0) {
            return true;
        }
        String url = value.toString();
        if (! (url.startsWith("http://") || url.startsWith("https://"))) {
            url = "http://" + url;
        }
        UrlValidator v = new UrlValidator(new String[]  {"http","https"});
        if (!v.isValid(url)) {
            setMessage("Not a valid URL");
            return false;
        }
        else {
            return true;
        }
    }

}
