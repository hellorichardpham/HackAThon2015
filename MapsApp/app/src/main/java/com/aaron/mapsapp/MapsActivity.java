package com.aaron.mapsapp;

import android.content.res.Resources;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Marker userLoc;
    GoogleApiClient mGoogleApiClient;
    GoogleMap map0;
    Location userLocation;
    LocationRequest mLocationRequest;
    Log log;

    //Creates location request with an interval of Halfsecond using GPS
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setSmallestDisplacement(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //Starts the location updater
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    //Stops the location updater
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        map0 = mapFragment.getMap();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();
        startParser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();      //Stop updating the location
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {    //if api still connected start updating location
            startLocationUpdates();
        } else {
            mGoogleApiClient.connect();         //else reconnect client
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        startLocationUpdates();                 //Start updating location
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the next section.

    }

    public void moveCamera(GoogleMap map, double latitude, double longitude, int zoomLevel) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))      // Sets the center of the map to Mountain View
                .zoom(zoomLevel)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public Marker createMarker (GoogleMap map) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .visible(false));
        return marker;
    }

    public void createStopMarker (GoogleMap map, double lat, double lng) {
            map.addMarker(new MarkerOptions()
            .position(new LatLng(lat,lng))
            .title("STop"));
    }

    public void updateMarker (Marker marker, double latitude, double longitude) {
        marker.setPosition(new LatLng(latitude, longitude));
        marker.setVisible(true);
    }

    public void changeLocation (Location userLocation) {
        double latitude = userLocation.getLatitude();
        double longitude = userLocation.getLongitude();
        updateMarker(userLoc, latitude, longitude);
        moveCamera(map0, latitude, longitude, 19);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleApiClient.connect();
        userLoc = createMarker(map);

    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        if (userLocation != null) {
            log.e("MyActivity", "LOCATION CHANGED");
            log.e("MyActivity", Double.toString(userLocation.getLatitude()));
            log.e("MyActivity", Double.toString(userLocation.getLongitude()));
            changeLocation(userLocation);
        }
    }



    /////////////
    static String convertStreamToString(java.io.InputStream is) {
        @SuppressWarnings("resource")
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";

    }

    /*Class that keeps track of the longitude, latidude and name of a bus stop*/
    class stop {
        float lon;
        float lat;
        String name;
    }
    Map<String,stop> bus_stop_hash = new HashMap<String,stop>();

    /*Tracks the time a bus is supposed to arrive at each respective bus stop*/
    class trip{
        String[] time;
    }

    /*Contains the data for each bus route*/
    class route {
        //What Metro number and the name of the route
        int id;
        String name;
        /*The number of bus stops on the route and the id of each bus stop, from first stop to
        * last*/
        int stops;
        String[] stop_ids;
        //0 = Weekday, 1 = Saturday, 2 = Sunday
        //Needed for inserting data in the structure, don't use
        boolean[] day = new boolean[3];

        /*This is where the trip class gets stored, the arrayList is used to store each type of route
        0 = Weekday, 1 = Saturday, 2 = Sunday. The data is stored in the hash table using the trip_id
        number, use an iterator to get info out DO NOT TRY AND USE trip_id TO GET INFORMATION OUT.*/
        List<Map<String,trip>> trips = new ArrayList<Map<String,trip>>();

        /*The Constructor, setup the List and sets the boolean days to false*/
        route(){
            for(int i = 0; i < 3; i++) {
                Map<String, trip> t = new HashMap<String, trip>();
                trips.add(i,t);
                day[i] = false;
            }
        }
    }
    Map<String,route> route_hash = new HashMap<String,route>();

    /*Temporary class and hash table needed to move data around. Do not use in the rest of the code.*/
    class tmp_stop_time{
        String[] time = new String[100];
        String[] stop_id = new String[100];
        int index;
        int day;
    }
    Map<String,tmp_stop_time> stop_time_tmp_hash = new HashMap<String,tmp_stop_time>();



    public void startParser() {
        //Used for debugging, not needed in final code
        TextView v = (TextView) findViewById(R.id.textView);

        /*Gets the data from the raw directories*/
        Resources r = getResources();
        InputStream Is = r.openRawResource(R.raw.stops);
        String stops = convertStreamToString(Is);
        InputStream Is1 = r.openRawResource(R.raw.stop_times);
        String stop_times = convertStreamToString(Is1);
        InputStream Is3 = r.openRawResource(R.raw.trips);
        String trips = convertStreamToString(Is3);

        /*Parses the information needed for the bus stops*/
        String[] s = stops.split("\n");
        for (int i = 0; i < s.length; ++i) {
            String[] line = s[i].split(",");
            stop newStop = new stop();
            newStop.lat = Float.parseFloat(line[4]);
            newStop.lon = Float.parseFloat(line[5]);
            newStop.name = line[1];
            bus_stop_hash.put(line[0], newStop);
        }

        /*Gets the data needed from stop_times and stores it in a temporary hashtable to be used
        in the next section*/
        String[] st = stop_times.split("\n");
        for (int i = 0; i < st.length; ++i) {
            String[] line = st[i].split(",");
            String line_parser = line[0];
            tmp_stop_time stop_time_route = new tmp_stop_time();
            String[] day = line[0].split("-");

            /*Setup which day it is*/
            if (day[3].equals("Weekday")) stop_time_route.day = 0;
            else if (day[3].equals("Saturday")) stop_time_route.day = 1;
            else if (day[3].equals("Sunday")) stop_time_route.day = 2;

            int j = 0;
            boolean inc = false;

            /*Goes through the list for each trip_id and puts its time and stop id into the
            * temporary data structure*/
            while (line[0].equals(line_parser) && !((i + 1) == st.length)) {
                inc = true;
                stop_time_route.stop_id[j] = line[3];
                stop_time_route.time[j] = line[2];
                stop_time_route.index = j;
                ++j;
                ++i;
                line = st[i].split(",");
            }
            //Needed because the while loop increments one time too many
            if (inc == true) --i;

            stop_time_tmp_hash.put(line_parser, stop_time_route);
        }


        /*Fills in the route_hash hash table, by going through each time a bus leaves the
         * metro station */
        String[] t = trips.split("\n");
        for (int i = 0; i < t.length; ++i) {
            /*Split the .txt by line*/
            String[] line = t[i].split(",");

            /*Used to get the metro route number*/
            String[] route_num = line[0].split("-");

            /*If the new trip does not is on a route that is not in the map*/
            if (route_hash.containsKey(route_num[0]) == false) {

                /*Create the new route and setup the basic information*/
                route new_route = new route();
                new_route.id = Integer.parseInt(route_num[0]);
                new_route.name = line[3];

                /*Get the information gathered from the previous loop and place in what the stops
                * are for the route*/
                tmp_stop_time stop_time_route = stop_time_tmp_hash.get(line[2]);
                new_route.stops = stop_time_route.index;
                new_route.stop_ids = new String[new_route.stops];
                for (int j = 0; j < new_route.stops; ++j) {
                    new_route.stop_ids[j] = stop_time_route.stop_id[j];
                }

                /*Not needed, too tired to take out*/
                new_route.day[stop_time_route.day] = true;


                /*Fill in the specifics for this metro bus trip*/
                trip newtrip = new trip();
                newtrip.time = new String[new_route.stops];
                /*Transfers the time from the temporary data structure to the permanent data
                 structure*/
                for (int j = 0; j < new_route.stops; ++j) {
                    newtrip.time[j] = stop_time_route.time[j];
                }

                //Put the new trip in the route
                new_route.trips.get(stop_time_route.day).put(line[2], newtrip);

                //Put the route in the hash table
                route_hash.put(route_num[0], new_route);

            } else if (route_hash.get(route_num[0]).day[stop_time_tmp_hash.get(line[2]).day] == false) {
                /*If the route is setup but not for that particular day it goes here, probably not
                * needed, but it felt like it was at 5am*/
                /*Fill in the specifics for this metro bus trip*/
                trip newtrip = new trip();
                newtrip.time = new String[stop_time_tmp_hash.get(line[2]).index];
                /*Transfers the time from the temporary data structure to the permanent data
                 structure*/
                for (int j = 0; j < newtrip.time.length; ++j) {
                    newtrip.time[j] = stop_time_tmp_hash.get(line[2]).time[j];
                }

                //Not needed to lazy
                route_hash.get(route_num[0]).day[stop_time_tmp_hash.get(line[2]).day] = true;
                //Put the new trip in the route
                route_hash.get(route_num[0]).trips.get(stop_time_tmp_hash.get(line[2]).day).put(line[2], newtrip);
                //v.setText("There");
            } else {
                /*Fill in the specifics for this metro bus trip*/
                trip newtrip = new trip();
                newtrip.time = new String[stop_time_tmp_hash.get(line[2]).index];
                /*Transfers the time from the temporary data structure to the permanent data
                 structure*/
                for (int j = 0; j < newtrip.time.length; ++j) {
                    newtrip.time[j] = stop_time_tmp_hash.get(line[2]).time[j];
                }
                //Put the new trip in the route
                route_hash.get(route_num[0]).trips.get(stop_time_tmp_hash.get(line[2]).day).put(line[2], newtrip);
            }
        }

        //0 Weekday trips.get(0).values()
        //1 Saturday
        //2 Sunday
        Collection<trip> col = route_hash.get("16").trips.get(0).values();
        Iterator<trip> tri = col.iterator();

        DateTime tempDateTime = null;
        int busindex = -1;
        Boolean isFound = false;
       /*itterate through your trip list and find the bus location depending on the current
        time*/
        while (tri.hasNext() && !isFound) {
            trip printtrip = tri.next();
            for (int i = 0; i < printtrip.time.length; ++i) {
                String time = printtrip.time[i];
                if(time.substring(0,1).equals("24")) {
                    String newTime = "00";
                    time = newTime.concat(time.substring(2));
                }

                tempDateTime = DateTime.parse(time, DateTimeFormat.forPattern("HH:mm:ss"));
                if (findNextBusTime(tempDateTime)) {

                    log.v("MainActivity", i + "***");
                    busindex = i;
                    isFound = true;
                    break;
                }
            }
            if (isFound) {
                break;
            }
        }


        //Generates XY coordinates of all bus routes for intial pins on GMaps
        String busStopLoc;
        double yaxis,xaxis;
        for (int i = 0; i < route_hash.get("16").stops; i++) {
            busStopLoc= route_hash.get("16").stop_ids[i];
            yaxis = bus_stop_hash.get( busStopLoc).lon;
            xaxis = bus_stop_hash.get( busStopLoc).lat;
            //Aaron's code goes here.
            System.out.println("Bus Stop ID: " + busStopLoc);
            System.out.println("x: " + xaxis + " y: " + yaxis);
            createStopMarker(map0, xaxis, yaxis);
        }

        //Generates The XY coordinates for the next closest bus stop
        String uniqueBusStopID = route_hash.get("16").stop_ids[busindex];
        String busStopName	= bus_stop_hash.get(uniqueBusStopID).name;
        //String busTripName = route_hash.get("16").trips.get()
        yaxis = bus_stop_hash.get(uniqueBusStopID).lon;
        xaxis = bus_stop_hash.get(uniqueBusStopID).lat;


        System.out.println("UNIQUE Bus Stop ID: " + uniqueBusStopID);
        System.out.println("x: " + xaxis + " y: " + yaxis);
        //v.setText(route_hash.get("16").trips.get(1).get("2961849-20152D-vs20152D-Saturday-07").time[0]);
    }

    /*we are checking(return t/f) if our current time is the same or after for every bus stop entry/time
    when it departs from the metro station*/
    public static Boolean findNextBusTime(DateTime timed){
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
        DateTime currentTime = new DateTime();
        if(isDuringOrAfter(currentTime,timed)){
            System.out.println("Next Time: " + fmt.print(timed));
            return true;
        }
        return false;
    }

    public static boolean isDuringOrAfter(DateTime currentTime,DateTime targetTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
        int result = DateTimeComparator.getTimeOnlyInstance().compare(targetTime,currentTime);

        /*return the appropriate boolean statements depending on whether the result is before,after
        or never happens
        */
        switch (result) {
            case 0:
                //System.out.println("Current time is equal to target time. " + fmt.print(targetTime));
                return true;
            case 1:
                //System.out.println("Current time is after or equal to target time. " + fmt.print(targetTime));
                return true;
            case -1:
                //System.out.println("returning false for " + fmt.print(targetTime));
                return false;
            default:
                //System.out.println("wow");
                return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
