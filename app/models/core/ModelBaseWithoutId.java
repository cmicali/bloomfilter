package models.core;

import com.google.gson.JsonObject;
import play.db.jpa.GenericModel;

import javax.persistence.MappedSuperclass;

/**
 * Author: chrismicali
 */
@MappedSuperclass
public abstract class ModelBaseWithoutId extends GenericModel implements SerializableToJson {

    public JsonObject toJson() {
        return toJson(JsonResponseType.Normal);
    }

    public JsonObject toJson(JsonResponseType responseType) {
        JsonObject jo = new JsonObject();
        return jo;
    }

}
