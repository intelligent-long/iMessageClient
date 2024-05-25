package com.longx.intelligent.android.ichat2.media.helper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by LONG on 2024/2/4 at 8:09 PM.
 */
public class LocationHelper {

    public static String getLocationNameFromCoordinates(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String locationName = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locality = address.getLocality();
                String subLocality = address.getSubLocality();
                String thoroughfare = address.getThoroughfare();
                StringBuilder sb = new StringBuilder();
                if(locality != null){
                    if(sb.length() != 0){
                        sb.append(" ");
                    }
                    sb.append(locality);
                }
                if(subLocality != null){
                    if(sb.length() != 0){
                        sb.append(" ");
                    }
                    sb.append(subLocality);
                }
                if(thoroughfare != null){
                    if(sb.length() != 0){
                        sb.append(" ");
                    }
                    sb.append(thoroughfare);
                }
                locationName = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationName;
    }

    public static double convertGpsStringToDegrees(String gpsString) {
        String[] parts = gpsString.split(",");
        if (parts.length != 3) {
            return 0.0;
        }
        String[] degreesParts = parts[0].split("/");
        String[] minutesParts = parts[1].split("/");
        String[] secondsParts = parts[2].split("/");

        double degrees = Double.parseDouble(degreesParts[0].trim()) / Double.parseDouble(degreesParts[1].trim());
        double minutes = Double.parseDouble(minutesParts[0].trim()) / Double.parseDouble(minutesParts[1].trim());
        double seconds = Double.parseDouble(secondsParts[0].trim()) / Double.parseDouble(secondsParts[1].trim());

        return degrees + (minutes / 60.0) + (seconds / 3600.0);
    }
}
