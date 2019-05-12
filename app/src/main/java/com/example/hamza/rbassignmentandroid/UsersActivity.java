package com.example.hamza.rbassignmentandroid;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.maps.model.MarkerOptions;

public class UsersActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle bundle = getIntent().getExtras();
        float lat = bundle.getFloat("lat");
        float lng = bundle.getFloat("lng");

        //   Add a marker on Location and move the camera
        LatLng latlng = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(latlng).title("Location")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconlocation));
      //  mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12.0f));

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
}
