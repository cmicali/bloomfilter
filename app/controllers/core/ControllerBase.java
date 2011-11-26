package controllers.core;

import core.util.Log;
import models.User;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: chrismicali
 */
public class ControllerBase extends Controller {


    //TODO: Put this in a config variable
    protected static final boolean TIMING_ENABLED = true;

    @Before
    private static void onRequestStart() {
        timingStartRequest();
        Log.debug("%s -> %s(%s)", request, request.action, request.routeArgs);
    }

    protected static void timingStartRequest() {
        if (TIMING_ENABLED) {
            getTimingMap().put("request_start", new Long(System.currentTimeMillis()));
        }
    }

    protected static void timeSet(String key, Long time) {
        if (TIMING_ENABLED) {
            getTimingMap().put(key, time);
        }
    }

    protected static void timingMark(String key) {
        if (TIMING_ENABLED) {
            long startTime = getTimingMap().get("request_start");
            getTimingMap().put(key, new Long(System.currentTimeMillis() - startTime));
        }
    }

    protected static void timeStart(String key) {
        if (TIMING_ENABLED) {
            getTimingMap().put("start_" + key, new Long(System.currentTimeMillis()));
        }
    }

    protected static void timeEnd(String key) {
        if (TIMING_ENABLED) {
            Long start = getTimingMap().get("start_" + key);
            if (start == null) throw new RuntimeException("timeStart was never called wtih key=" + key);
            getTimingMap().remove("start_" + key);
            getTimingMap().put(key, new Long(System.currentTimeMillis() - start.longValue()));
        }
    }

    protected static Map<String, Long> getTimingMap() {
        Map<String, Long> map;
        if (TIMING_ENABLED) {
            map = (Map<String, Long>)request.args.get("timing_hash");
            if (map == null) {
                map = new HashMap<String, Long>();
                request.args.put("timing_hash", map);
            }
        }
        return map;
    }


}
