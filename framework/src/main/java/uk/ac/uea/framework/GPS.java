package uk.ac.uea.framework;

        import android.location.Location;

        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;

        import org.json.JSONObject;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;


public interface GPS {


    // GETTERS
    ArrayList getMarkerPoints();
    String getDirectionsUrl(LatLng origin, LatLng dest);
    Location getUserLocation();
    Location getDestinationLocation();


    // SETTERS
    void setUserLocation(Location userLocation);
    void setDestinationLocation(Location givenDest);
    void setDirectionsUrl(String url);
    void setMarkerPoints(ArrayList markers);


    // OTHER METHODS
    /**
     *
     * @param location
     */
    void addMarkerToTheMap(Location location);
    /**
     *
     */
    ArrayList<MarkerOptions> readMap() throws IOException;
    /**
     *
     * @param radius
     * @return
     */
    boolean checkProximity(float radius);
    /**
     void setMapIfNeeded();

     /**
     *
     * @param location
     */
    MarkerOptions saveLocationAsDestination(Location location, String markerName);
    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     * @param jObject
     * @return
     */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject);
    /**
     *
     * @param encoded
     * @return
     */
    public List<LatLng> decodePoly(String encoded) ;



}
