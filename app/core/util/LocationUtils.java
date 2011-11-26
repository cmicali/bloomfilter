package core.util;

import models.core.Location;

/**
 * Author: chrismicali
 */
public class LocationUtils {

    public enum LocationUnit { Miles, Kilometers }

    public static double distance(Location loc1, Location loc2, LocationUnit unit) {
        return distance(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude(), unit);
    }
    
    private static double distance(double lat1, double lon1, double lat2, double lon2, LocationUnit unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == LocationUnit.Kilometers) {
            dist = dist * 1.609344;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts decimal degrees to radians             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts radians to decimal degrees             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
