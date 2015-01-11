package com.aaron.mapsapp;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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


public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Marker userLoc;
    GoogleApiClient mGoogleApiClient;
    GoogleMap map0;
    Location userLocation;
    LocationRequest mLocationRequest;
    Log log;
    double initLat;
    double initLng;

    //Creates location request with an interval of Halfsecond using GPS
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(400);
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
        //Import data from previous activity
        Bundle extras = getIntent().getExtras();

        //Find out what route the user wants
        int routeNum = extras.getInt("ROUTE");
        if (routeNum == 10) {
            setTitle("Route 10");
        }else if (routeNum == 16) {
            setTitle("Route 16");
        }

        //Get the current location from previous activity
        initLat = extras.getDouble("Lat");
        initLng = extras.getDouble("Lng");


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        map0 = mapFragment.getMap();
        map0.setMyLocationEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();
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
        Location initLoc = new Location("");
        initLoc.setLatitude(initLat);
        initLoc.setLongitude(initLng);
        changeLocation(initLoc);

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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
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
