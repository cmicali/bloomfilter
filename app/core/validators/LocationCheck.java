package core.validators;

import play.data.validation.Check;

/**
 * Author: chrismicali
 */
public class LocationCheck extends Check {

    @Override
    public boolean isSatisfied(Object validatedObject, Object ll) {
        try {
            String loc = (String)ll;
            String[] parts = loc.split(",");
            if (parts.length == 2) {
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);
                if (validate(latitude, longitude)) {
                    return true;
                }
            }
        }
        catch(Exception ex) {

        }
        String val = null;
        if (ll != null) val = ll.toString();
        setMessage("Invalid location: expecting [lat,long] got: [" + val + "]");
        return false;
    }

    private boolean validate(double latitude, double longitude) {
        return (latitude >= -90.0 && latitude <= 90.0) && (longitude >= -180.0 && longitude <= 180.0);
    }

}
