package com.aaron.mapsapp;

import java.lang.String;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.Api;
import android.os.Bundle;
import android.location.*;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import com.google.android.gms.common.api.GoogleApiClient;
import android.util.Log;
//import org.apache.commons.logging.Log;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    GoogleApiClient mGoogleApiClient;
    GoogleMap map0;
    Location userLocation;
    double mLatitudeText;
    double mLongitudeText;

    Log log;

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

        //mGoogleApiClient.connect();

    }


    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        userLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (userLocation != null) {
            log.e("MyActivity","Here");
            mLatitudeText = userLocation.getLatitude();
            mLongitudeText = userLocation.getLongitude();
            changeLocation(userLocation);
        }
        log.e("MyActivity",Double.toString(mLatitudeText));
        log.e("MyActivity",Double.toString(userLocation.getLatitude()));

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
        moveCamera(map0,latitude,longitude,17);

    }
    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleApiClient.connect();
        log.e("MyActivity",Double.toString(mLatitudeText));
    }
}
