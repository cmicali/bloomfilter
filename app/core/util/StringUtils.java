package core.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.time.DurationFormatUtils;
import play.libs.Codec;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TimeZone;

/**
 * Author: chrismicali
 */
public class StringUtils {

    public static String cleanStringForPermalink(String str) {
        str = str.trim();
        str = str.replaceAll("[^A-Za-z0-9\\s]", "");
        str = str.replace(' ', '_');
        return str.toLowerCase();
    }

    public static String getFriendlyElapsedTimeString(long elapsedTimeMs) {
        return String.format("%s", DurationFormatUtils.formatDurationWords(elapsedTimeMs, true, true));
    }

    public static String getFriendlyTimeSinceEvent(long eventTime) {

        String pattern;
        String text;

        long elapsed = System.currentTimeMillis() - eventTime;
        
        // Less than 1 hr
        if (elapsed < (1000 * 60) * 60) {
            pattern = "m";
            text = "minute";
        }
        else if (elapsed < (1000 * 60) * 60 * 24) {
            pattern = "H";
            text = "hour";
        }
        else {
            pattern = "d";
            text = "day";
        }
        
        int num = Integer.parseInt(DurationFormatUtils.formatPeriod(eventTime, System.currentTimeMillis(), pattern));
        String suffix = "";

        if (num > 1) {
            suffix = "s";
        }
        if (num == 0)
            return "just now";
        else
            return String.format("%d %s%s ago", num, text, suffix);
        
    }

    public static int fuzzyCompareTo(String s1, String s2) {
        s1 = s1.replaceAll("[^A-Za-z0-9]", "");
        s2 = s2.replaceAll("[^A-Za-z0-9]", "");
        return s1.compareToIgnoreCase(s2);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }

    public static byte[] base64UrlDecode(String s) {
        s = s.replace('-', '+').replace('_', '/');
        int pad = 4 - (s.length() % 4);
        for(int i = 0; i < pad; i++)
            s += "=";
        return Codec.decodeBASE64(s);
    }

    public static JsonElement base64UrlDecodeToJson(String s) {
        try {
            String json = new String(base64UrlDecode(s), "UTF-8");
            JsonParser parser = new JsonParser();
            return parser.parse(json);
        }
        catch(UnsupportedEncodingException ex) {

        }
        return null;
    }

    public static String urlEncode(String s) {
        try {
            s = URLEncoder.encode(s, "UTF8");
        }
        catch(Exception ex) {

        }
        return s;
    }

}
