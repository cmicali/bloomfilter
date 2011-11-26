package controllers.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.util.Log;
import models.core.ModelBase;
import models.core.SerializableToJson;
import play.db.jpa.JPA;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Author: chrismicali
 */
public class ApiBase extends ControllerBase {

    protected static String getResponseString (JsonObject response) {
        return getResponseString(getRequestObject(true), response);
    }

    protected static void endRequest(JsonObject response) {
        end(getRequestObject(true), response);
    }

    protected static void endRequest(String objectName, SerializableToJson object) {
        endRequest(objectName, object, SerializableToJson.JsonResponseType.Normal);
    }

    protected static void endRequest(String objectName, SerializableToJson object, SerializableToJson.JsonResponseType jsonResponseType) {
        JsonObject jo = new JsonObject();
        jo.add(objectName, object.toJson(jsonResponseType));
        endRequest(jo);
    }

    protected static void endRequest(String listName, List<? extends SerializableToJson> objectList) {
        JsonObject jo = new JsonObject();
        JsonArray ja = new JsonArray();
        jo.addProperty("count", objectList.size());
        for(SerializableToJson o : objectList) {
            ja.add(o.toJson());
        }
        jo.add(listName, ja);
        endRequest(jo);
    }

    protected static void endRequest(String responseString) {
        renderJSON(responseString);
    }

    protected static boolean validate() {
        boolean isError = validation.hasErrors();
        if (isError) {
            Log.debug("validation failed");
            failRequestValidationFailed();
        }
        else {
            Log.debug("validation passed");
        }
        return !isError;
    }

    protected static boolean validateModel(Long id, Class<? extends ModelBase> modelClazz) {
        Method m = null;
        ModelBase model = null;
        try {
            m = modelClazz.getMethod("findById", Object.class);
            model = (ModelBase) m.invoke(null, id);
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e) {
        } catch (IllegalAccessException e) {
        }
        return validateFound(id, model, modelClazz);
    }

    protected static boolean validateFound(Long id, ModelBase fetchedObject, Class modelClass) {
        return validateFound(Long.toString(id), fetchedObject, modelClass);
    }

    protected static boolean validateFound(String id, ModelBase fetchedObject, Class modelClass) {
        if (fetchedObject == null) {
            failRequest("No %s found with id=%s", modelClass.getSimpleName().toLowerCase(), id);
            return false;
        }
        return true;
    }

    protected static void failRequestValidationFailed() {
        failRequest(null, "Invalid parameters: " + validation.errorsMap().toString());
    }

    protected static void failRequest(String errorReason) {
        failRequest(null, errorReason);
    }

    protected static void failRequest(String errorReason, Object... vars) {
        failRequest(String.format(errorReason, vars));
    }

    protected static void failRequest(JsonObject response, String errorReason) {
        JsonObject request = getRequestObject(false);
        request.addProperty("error_reason", errorReason);
        JPA.setRollbackOnly();
        end(request, response);
    }

    private static void end(JsonObject request, JsonObject response) {
        renderJSON(getResponseString(request, response));
    }

    protected static String getResponseString(JsonObject request, JsonObject response) {
        JsonObject res = new JsonObject();
        res.add("request", request);
        res.add("response", response);
        Gson g = new Gson();
        return g.toJson(res);
    }

    private static JsonObject getRequestObject(boolean requestOk) {
        JsonObject req = new JsonObject();
        req.addProperty("status", requestOk ? "ok" : "error");
        if (TIMING_ENABLED) {
            Map<String, Long> map = getTimingMap();
            long startTime = map.get("request_start");
            map.remove("request_start");
            long elapsed = System.currentTimeMillis() - startTime;
            JsonObject timing = new JsonObject();

            for(String key : map.keySet()) {
                long time = map.get(key);
                timing.addProperty(key, time + "ms");
            }

            timing.addProperty("request_complete", elapsed + "ms");

            req.add("timing", timing);
        }
        return req;
    }

}
