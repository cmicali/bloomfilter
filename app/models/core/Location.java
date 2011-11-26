package models.core;

import com.google.gson.JsonObject;

import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
public class Location implements Serializable {

    private Double latitude;
    private Double longitude;

    protected Location() {

    }

    public Location(String ll) {
        setLL(ll);
    }

    public Location(Double lat, Double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }

    public void setLL(String ll) {
        String[] parts = ll.split(",");
        if (parts.length == 2) {
            setLatitude(Double.parseDouble(parts[0]));
            setLongitude(Double.parseDouble(parts[1]));
        }
    }

    public static Location fromLl(String ll) {
        return new Location(ll);
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("lat", getLatitude());
        jo.addProperty("lon", getLongitude());
        return jo;
    }

    @Override
    public String toString() {
        return latitude.toString() + "," + longitude.toString();
    }
    
}
