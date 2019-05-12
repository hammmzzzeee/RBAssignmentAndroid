package com.example.hamza.rbassignmentandroid;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks{

    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;

    //Location
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        //set on click listener on Google sign in button
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        // Location
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            showAlert("Login Failed");
        }
    }

    private void updateUI(GoogleSignInAccount account){

          //Login successful move to Maps Activity
          if (account != null) {

              Location location = new Location();
              location.username = account.getEmail();

              DBCommon.User user = new DBCommon.User(this);

              // insert name and email in db if not already in db
              if(!user.CheckIsDataAlreadyInDBorNot(account.getEmail())){
                  user.insert(account.getDisplayName(), account.getEmail());
                  writePost("post", "yes");
                  }



              Intent intent = new Intent(this, MapsActivity.class);
              startActivity(intent);
          }


    }

    public void  showAlert(String message){
        //show alert
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //finish();
                    }
                }).setNegativeButton("", null).show();
    }


    // Location
    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // Shared prefs
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
