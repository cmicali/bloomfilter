package models.core;

import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Author: chrismicali
 */
@MappedSuperclass
public abstract class TimestampedModelBase extends CreatedTimestampModelBase {

    @Column(nullable = false)
    public Date date_modified;

    public TimestampedModelBase() {
        super();
    }

    @Override
    public void touch() {
        super.touch();
        date_modified = new Date();
    }

    public JsonObject toJson(JsonResponseType responseType, boolean addDateCreated, boolean addDateModified) {
        JsonObject jo = super.toJson(responseType, addDateCreated, addDateModified);
        if (addDateModified) {
            jo.addProperty("date_modified", date_modified.getTime());
        }
        return jo;
    }


}
