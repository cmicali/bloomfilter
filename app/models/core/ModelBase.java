package models.core;

import com.google.gson.JsonObject;
import play.db.jpa.Model;

import javax.persistence.MappedSuperclass;

/**
 * Author: chrismicali
 */
@MappedSuperclass
public abstract class ModelBase extends Model implements SerializableToJson {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JsonObject toJson() {
        return toJson(SerializableToJson.JsonResponseType.Normal);
    }
    
    public JsonObject toJson(SerializableToJson.JsonResponseType responseType) {
        // Log.debug("toJson: %s id=%d", getClass().getSimpleName(), getId());
        JsonObject jo = new JsonObject();
    	jo.addProperty("id", getId());
        return jo;
    }
}
