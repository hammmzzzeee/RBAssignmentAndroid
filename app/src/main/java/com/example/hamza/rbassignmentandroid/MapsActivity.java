package com.example.hamza.rbassignmentandroid;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;



    private Location location;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


//    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
//        ArrayList<String> result = new ArrayList<>();
//
//        for (String perm : wantedPermissions) {
//            if (!hasPermission(perm)) {
//                result.add(perm);
//            }
//        }
//
//        return result;
//    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            //  locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    Location selectedLocation;


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {

            //post request
            // check if user already posted to server
            if(readPost("post").equals("yes")){
                writePost("post", "no");

                DBCommon.User user = new DBCommon.User(this);
                ArrayList<com.example.hamza.rbassignmentandroid.Location> arrlocations = user.getFromDB();
                if (arrlocations.size()>0){
                    com.example.hamza.rbassignmentandroid.Location loc = arrlocations.get(0);


                    String username  =  loc.username;
                    String email =  loc.email;
                    double latitude =  location.getLatitude();
                    double longitude =  location.getLongitude();

                    com.example.hamza.rbassignmentandroid.Location locObj = new com.example.hamza.rbassignmentandroid.Location();
                    locObj.username = username;
                    locObj.email = email;
                    locObj.lat =  Float.valueOf(String.valueOf(latitude));
                    locObj.lng = Float.valueOf(String.valueOf(longitude));



                    // Get instance of Network manager and post user location
                    NetworkManager.getInstance(this).postData(locObj);
            }

            }

               // Add marker on points
              addMarker(location.getLatitude(), location.getLongitude());
             }
    }

    public void addMarker(double latitude, double longitude){
        //Show location on Map
        LatLng latlng = new LatLng(latitude, longitude);

        write("loc", latitude, longitude);
        // Adding marker and Changing marker icon

        mMap.addMarker(new MarkerOptions().position(latlng).title("Location")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconlocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

        //Move the camera to the user's location and zoom in!
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));

    }

    // Location

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(MapsActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }


    public void btnAllUsersClicked(View view) {
        Intent intent = new Intent(this, AllUsersOnMapActivity.class);
        startActivity(intent);
    }

    public void btnAllPositionsClicked(View view) {
        Intent intent = new Intent(this, UsersPositionsActivity.class);
        startActivity(intent);
    }

    // Shared prefs
    public void write(String key, double lat, double lng){
        SharedPreferences.Editor editor = getSharedPreferences(key, MODE_PRIVATE).edit();
        editor.putFloat("lat", (float) lat);
        editor.putFloat("lat", (float) lng);
        editor.apply();


    }
    public com.example.hamza.rbassignmentandroid.Location read(String key){
        com.example.hamza.rbassignmentandroid.Location loc = new com.example.hamza.rbassignmentandroid.Location();
        loc.lat = Float.valueOf(0);
        loc.lng = Float.valueOf(0);
        SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);
        Float restoredText = prefs.getFloat(key, 0);
        if (restoredText != null) {
            float lat = prefs.getFloat("lat", 0); //0 is the default value.
            float lng = prefs.getFloat("lng", 0); //0 is the default value.
            loc.lat = lat;
            loc.lng = lng;

        }
        return loc;
    }
    public void writePost(String key, String post){
        SharedPreferences.Editor editor = getSharedPreferences(key, MODE_PRIVATE).edit();
        editor.putString(key, post);
        editor.apply();

    }
    public String readPost(String key){

        String post = "";
        SharedPreferences prefs = getSharedPreferences(key, MODE_PRIVATE);
        String restoredText = prefs.getString(key, null);
        if (restoredText != null) {
            post = prefs.getString(key, ""); //0 is the default value.

        }
        return post;
    }
}
