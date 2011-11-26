package core.util;

import play.Logger;

/**
 * Author: chrismicali
 */
public class Log {

    protected static boolean LOGGING_ENABLED = true;

    public static void debug(String message, Object... args) {
        if (Log.LOGGING_ENABLED) {
            Logger.debug(message, args);
        }
    }

    public static void error(String message, Object... args) {
        if (Log.LOGGING_ENABLED) {
            Logger.error(message, args);
        }
    }

}
