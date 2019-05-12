package com.example.hamza.rbassignmentandroid;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class AllUsersOnMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    ArrayList<Location> arrLocations;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_on_map);
// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();

        // Get instance of Network manager and get All locations
        NetworkManager.getInstance(this).getAllLocations( new InterfacePositions<ArrayList<Location>>()
        {
            @Override
            public void getResult(ArrayList<Location> arrLoc)
            {
                if (arrLoc.size() > 0 )
                {
                    //setting data
                    arrLocations = arrLoc;
                    addAllMarkersOnMap();


                }
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void addAllMarkersOnMap(){


        // adding all markers on map
        for (int i =0; i < arrLocations.size(); i++) {
            Location loc = arrLocations.get(i);
            createMarker(loc.lat, loc.lng, loc.username, "", R.drawable.iconlocation);
            }
        //Move  camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arrLocations.get(1).lat, arrLocations.get(1).lng), 10.0f));


    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(iconResID)));
    }


}
