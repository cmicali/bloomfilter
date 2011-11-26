package models.core;

import com.google.gson.JsonObject;
import play.db.jpa.JPABase;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Author: chrismicali
 */
@MappedSuperclass
public abstract class CreatedTimestampModelBase extends ModelBase {

    @Column(nullable = false)
    public Date date_created;

    public CreatedTimestampModelBase() {
        super();
        date_created = new Date();
        touch();
    }

    public void touch() {
    }

    @Override
    public <T extends JPABase> T save() {
        touch();
        return super.save();
    }

    @Override
    public JsonObject toJson(JsonResponseType responseType) {
        return toJson(responseType, true, true);
    }

    public JsonObject toJson(JsonResponseType responseType, boolean addDateFields) {
        return toJson(responseType, addDateFields, addDateFields);
    }

    public JsonObject toJson(JsonResponseType responseType, boolean addDateCreated, boolean addDateModified) {
        JsonObject jo = super.toJson(responseType);
        if (addDateCreated) {
            jo.addProperty("date_created", date_created.getTime());
        }
        return jo;
    }

}
