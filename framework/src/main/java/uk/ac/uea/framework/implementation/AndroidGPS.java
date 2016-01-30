package uk.ac.uea.framework.implementation;

import uk.ac.uea.carfinder.DirectionsJSONParser;
import uk.ac.uea.carfinder.MapsActivity;
import uk.ac.uea.framework.GPS;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Beren on 30/01/2016.
 */


public class AndroidGPS implements GPS{

    ArrayList markerPoints = new ArrayList<LatLng>();
    Location userLocation = null;
    Location destinationLocation = null;
    String directionsUrl = null;
    GoogleMap gMap = null;
    Activity activity;
    Context context;


    public AndroidGPS(Context current) {
        this.context = current;
    }

    @Override
    public ArrayList getMarkerPoints() {
        return this.markerPoints;
    }

    @Override
    public GoogleMap getgMap() {
        return this.gMap;
    }

    @Override
    public String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for(int i=2;i<markerPoints.size();i++){
            LatLng point  = (LatLng) markerPoints.get(i);
            if(i==2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }


        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;
    }

    @Override
    public Location getUserLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        this.userLocation = locationManager.getLastKnownLocation(provider);
        return this.userLocation;
    }

    @Override
    public Location getDestinationLocation() {
        return this.destinationLocation;
    }

    @Override
    public void setUserLocation(Location givenLocation) {
        this.userLocation = givenLocation;
    }

    @Override
    public void setDestinationLocation(Location givenDest) {

        this.destinationLocation = givenDest;

    }

    @Override
    public void setDirectionsUrl(String url) {
        this.directionsUrl = url;
    }

    @Override
    public void setMarkerPoints(ArrayList markers) {
        this.markerPoints = markers;
    }

    @Override
    public void setgMap(GoogleMap givenMap) {
        this.gMap = givenMap;
    }

    /**
     * @param location
     */
    @Override
    public void addMarkerToTheMap(Location location) {
        markerPoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    /**
     *
     */
    @Override
    public void readMap() throws IOException {
        String[] columns = new String[10];

        BufferedReader br = null;

        String line = "";
        String cvsSplitBy = ",";

        InputStream stream = context.getResources().openRawResource(uk.ac.uea.carfinder.R.raw.mapdata);
        br = new BufferedReader(new InputStreamReader(stream));


        while ((line = br.readLine()) != null) {

            columns = line.split(cvsSplitBy);


            float lat = Float.parseFloat(columns[2]);
            float longt = Float.parseFloat(columns[3]);
            String title = columns[1];
            MarkerOptions markerOption = new MarkerOptions();

            markerOption.position(new LatLng(lat,longt)).title(title);

            gMap.addMarker(markerOption);

        }
    }

    /**
     * @param radius
     * @return
     */
    @Override
    public boolean checkProximity(float radius) {
        return (this.userLocation.distanceTo(this.destinationLocation) <= radius);
    }


    /**
     * @param location
     */
    @Override
    public void saveLocationAsDestination(Location location, String markerName) {
        setDestinationLocation(location);
        gMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));

        gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        gMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(markerName));
    }

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     *
     * @param jObject
     * @return
     */
    @Override
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            // Traversing all routes
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                // Traversing all legs
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    // Traversing all steps
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        //PolylineOptions poption = new PolylineOptions().add(latLng).add(uea).width(10).color(Color.MAGENTA).geodesic(true);
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");

                        List<LatLng> list = decodePoly(polyline);

                        // Traversing all points
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }


        return routes;
    }

    /**
     * @param encoded
     * @return
     */
    @Override
    public List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }


        return poly;
    }

    @Override
    public void setUpMapIfNeeded() {

    }

    /**
     * Fetches data from url passed
     */
    public class DownloadTask extends AsyncTask<String, Void, String> {

        public String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            }catch(Exception e){
                // Log.d("Exception while downloading url", e.toString());
            }finally{
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }
    /**
     *  A class to parse the Google Places in JSON format
     *  */
    public  class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.MAGENTA);
            }

            // Drawing polyline in the Google Map for the i-th route
            gMap.addPolyline(lineOptions);
        }
    }



}


