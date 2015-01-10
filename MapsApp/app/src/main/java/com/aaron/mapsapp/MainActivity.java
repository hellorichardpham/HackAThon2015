package com.aaron.mapsapp;

import java.lang.String;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
//import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleApiClient client;
    Location userLocation;
    double mLatitudeText;
    double mLongitudeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();

    }


    @Override
    public void onMapReady(GoogleMap map) {

        userLocation = LocationServices.FusedLocationApi.getLastLocation(
                client);
        if (userLocation != null) {
            mLatitudeText = userLocation.getLatitude();
            mLongitudeText = userLocation.getLongitude();
        }

        map.addMarker(new MarkerOptions()
                .position(new LatLng(mLatitudeText, mLongitudeText))
                .title("Aaron's House"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mLatitudeText, mLongitudeText))      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                //.bearing(90)                // Sets the orientation of the camera to east
                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
