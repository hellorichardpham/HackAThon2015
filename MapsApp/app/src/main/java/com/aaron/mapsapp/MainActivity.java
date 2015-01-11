package com.aaron.mapsapp;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient mGoogleApiClient;
    GoogleMap map0;
    Location userLocation;
    LocationRequest mLocationRequest;
    Log log;

    //Creates location request with an interval of 10seconds using GPS
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
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
        setContentView(R.layout.activity_main);

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

    public void placeMarker(GoogleMap map, double latitude, double longitude) {
      map.addMarker(new MarkerOptions()
            .position(new LatLng(latitude, longitude))
            .title("Current Location"));
    }

    public void moveCamera(GoogleMap map, double latitude, double longitude, int zoomLevel) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))      // Sets the center of the map to Mountain View
                .zoom(zoomLevel)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void changeLocation (Location userLocation) {
        double latitude = userLocation.getLatitude();
        double longitude = userLocation.getLongitude();
        placeMarker(map0,latitude,longitude);
        moveCamera(map0,latitude,longitude,19);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        if (userLocation != null) {
            log.e("MyActivity", "LOCATION CHANGED");
            log.e("MyActivity", Double.toString(userLocation.getLatitude()));
            changeLocation(userLocation);
        }
    }
}
