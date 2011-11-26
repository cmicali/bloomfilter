package models.core;

import com.google.gson.JsonObject;

/**
 * Author: chrismicali
 */
public interface SerializableToJson {

    public enum JsonResponseType {
        Normal,
        Long
    }

    public JsonObject toJson();
    public JsonObject toJson(SerializableToJson.JsonResponseType responseType);

}
