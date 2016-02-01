package uk.ac.uea.carfinder;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button; 
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.uea.framework.sl.directions.*;
import uk.ac.uea.framework.sl.directions.pojos.DirectionsPojo;
import uk.ac.uea.framework.sl.directions.pojos.Legs;
import uk.ac.uea.framework.sl.directions.pojos.Routes;
import uk.ac.uea.framework.sl.directions.pojos.Steps;
import uk.ac.uea.framework.sl.utils.JsonGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements View.OnClickListener {

    private GoogleMap mMap;
    Location carLocation;
    Location myLocation = null;

    double time = 0;
    float distanceToCar = 0;

    boolean carFound = false;
    boolean alphaToggle = true;
    boolean myToggle = false;
    boolean readEnabled = false;

    String directions;
    String directionsURL;

    ImageButton saveCar;
    ImageButton simpleView;
    ImageButton advancedView;
    ImageButton help;
    TextView textView;
    TextView textView2;
    Chronometer chrono;



    ///////////////////////// MAP METHODS ///////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        final Handler mHandler = new Handler();
        Runnable updateUI = new Runnable() {
            @Override
            public void run() {

                updateMyLocation();
                if (carLocation != null && myLocation != null) {
                    CheckCarFound(5);

                }
                if (carFound)
                    chrono.stop();
                else
                    chrono.start();

                textView2.setText(String.valueOf((int)distanceToCar) + " meters");
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.post(updateUI);

        InitializeViewObjects();



/*
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

       @Override
       public void onMapClick(LatLng point) {
             if (markerPoints.size() >= 10) {
                 return;
             }

             markerPoints.add(point);

             MarkerOptions options = new MarkerOptions();

             options.position(point);
             options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
             mMap.addMarker(options);
       }}
        );
*/

        // The map will be cleared on long click
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                if (myToggle) {
                    try {

                        if (readEnabled)
                            ReadMap();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    mMap.clear();


                }
                myToggle = !myToggle;
            }
        });




        try {
            if(readEnabled)
                 ReadMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void InitializeViewObjects() {

        saveCar = (ImageButton) findViewById(R.id.button);
        simpleView = (ImageButton) findViewById(R.id.button2);
        advancedView = (ImageButton) findViewById(R.id.button3);
        help = (ImageButton) findViewById(R.id.button4);

        chrono = (Chronometer) findViewById(R.id.chronometer1);
        saveCar.setOnClickListener(this);
        simpleView.setOnClickListener(this);
        advancedView.setOnClickListener(this);
        help.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);

        textView.setAlpha(0);


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Called when a view has been clicked.
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                onSaveCar();

                break;
            case R.id.button2:
                if(myLocation != null && carLocation != null)
                {
                    LatLng origin = new LatLng(myLocation.getLatitude(),myLocation.getLongitude()) ;
                    LatLng dest =  new LatLng(carLocation.getLatitude(),carLocation.getLongitude()) ;

                    String url = null;
                    try {
                        url = getDirectionsUrl(origin, dest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);


                }
                break;
            case R.id.button3:

                if(myLocation != null && carLocation != null && directionsURL != null) {
                    LatLng origin = new LatLng(myLocation.getLatitude(),myLocation.getLongitude()) ;
                    LatLng dest =  new LatLng(carLocation.getLatitude(),carLocation.getLongitude()) ;

                    String url = null;
                    try {
                        url = getDirectionsUrl(origin, dest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if (alphaToggle)
                        textView.setAlpha(1);
                    else
                        textView.setAlpha(0);

                    alphaToggle = !alphaToggle;

                    try {
                        DirectionFinder();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    textView.setText(directions);
                    directions = null;
                }
                break;
            case R.id.button4:
                if(myLocation != null && carLocation != null)
                    textView2.setText("Distance : " + String.valueOf((int) distanceToCar) + " meters.");
                     //textView2.setText(( myLocation.getLatitude() + " " + myLocation.getLongitude() ) + "\n " + (carLocation.getLatitude() + " " + carLocation.getLongitude() ));
                break;
        }
    }



    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        mMap.setMyLocationEnabled(true);

        updateMyLocation();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        //  mMap.addMarker(new MarkerOptions().position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).title("You are here!"));

    }

    /**
     * UPDATES USER LOCATION
     */
    private void updateMyLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        myLocation = locationManager.getLastKnownLocation(provider);
    }

    /**
     * Reading the markers.
     *
     * @throws IOException
     */
    void ReadMap() throws IOException {

        String[] columns = new String[10];

        BufferedReader br = null;

        String line = "";
        String cvsSplitBy = ",";

        InputStream stream = getResources().openRawResource(R.raw.mapdata);
        br = new BufferedReader(new InputStreamReader(stream));

        while ((line = br.readLine()) != null) {

            columns = line.split(cvsSplitBy);


            float lat = Float.parseFloat(columns[2]);
            float longt = Float.parseFloat(columns[3]);
            String title = columns[1];
            MarkerOptions markerOption = new MarkerOptions();

            markerOption.position(new LatLng(lat, longt)).title(title);

            mMap.addMarker(markerOption);

        }

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) throws IOException {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints


        String walk = "avoid=highways&mode=walking";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor  + "&" + walk;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        directionsURL = data;
        return data;
    }
    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     *
     * @param jObject
     * @return
     */
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            // Traversing all routes
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                // Traversing all legs
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    // Traversing all steps
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";

                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");

                        List<LatLng> list = decodePoly(polyline);

                        // Traversing all points
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(list.get(l).latitude));
                            hm.put("lng", Double.toString(list.get(l).longitude));
                            path.add(hm);

                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }


        return routes;
    }

    /**
     * @param encoded
     * @return
     */
    private List<LatLng> decodePoly(String encoded) {

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

    public void DirectionFinder() throws IOException {
        List<String> d = new ArrayList<>();

        DirectionsPojo dp = (DirectionsPojo) JsonGenerator.generateTOfromJson(directionsURL, DirectionsPojo.class);
        for (Routes route : dp.getRoutes()) {

            d.add("----- Route Begins ------");
            //System.out.println("----- Route Begins ------");
            for (Legs leg : route.getLegs()) {
                d.add("Total Distance " + leg.getDistance().getText() + "\n");
                d.add(leg.getStart_address());
                //System.out.println("Total Distance "+leg.getDistance().getText());
                for (Steps step : leg.getSteps()) {

                    d.add(step.getDistance().getText());
                    d.add("Walk for: " + step.getDuration().getText());


                }
                d.add(leg.getEnd_address());
            }
            d.add("----- Route Ends ------");

        }
        d.add("\n");
        directions = "\n";
        for (int i = 0; i < d.size(); i++) {
            directions += d.get(i) + "\n";
        }

    }

    ///////////////////////// CAR METHODS ///////////////////////////

    /**
     * CHECKS IF USER IS WITHIN @param radius METERS OF THEIR CAR
     *
     * @param radius
     */
    void CheckCarFound(float radius) {
        distanceToCar = (myLocation.distanceTo(carLocation));
        if (distanceToCar <= radius) {
            carFound = true;
        }
        else
            carFound =false;
    }

    public void onSaveCar() {

        chrono.setBase((long) (SystemClock.elapsedRealtime() + time));
     //   if (!carFound)
      //      chrono.start();

        updateMyLocation();
        carLocation = myLocation;

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(carLocation.getLatitude(), carLocation.getLongitude());

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        mMap.addMarker(new MarkerOptions().position(new LatLng(carLocation.getLongitude(), carLocation.getLongitude())).title("My Car"));

    }

    ///////////////////////// INNER CLASSES /////////////////////////

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }


            return data;
        }

        // Executes in UI thread, after the execution of
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
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
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                //lineOptions.color(Color.MAGENTA);
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(7);
                lineOptions.color(Color.MAGENTA);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }


}

