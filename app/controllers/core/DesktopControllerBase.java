package controllers.core;

import play.mvc.Before;

/**
 * Author: chrismicali
 */
public class DesktopControllerBase extends WebControllerBase {

    @Before
    protected static void redirectToMobileSite() {
        if (getUserAgent().detectSmartphone()) {
            redirect("/mobile");
        }
    }

}
