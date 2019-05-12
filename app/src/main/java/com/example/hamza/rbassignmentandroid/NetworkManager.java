package com.example.hamza.rbassignmentandroid;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NetworkManager {

    private static NetworkManager instance = null;

    Context context;
    public RequestQueue requestQueue;


    public NetworkManager(Context context)
    {
        requestQueue = Volley.newRequestQueue(context);
        this.context = context;
        //other stuf if you need
    }

    public static synchronized NetworkManager getInstance(Context context)
    {
        if (null == instance)
            instance = new NetworkManager(context);
        return instance;
    }
//    //this is so you don't need to pass context each time
//    public static synchronized NetworkManager getInstance()
//    {
//        if (null == instance)
//        {
//            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
//                    " is not initialized, call getInstance(...) first");
//        }
//        return instance;
//    }



    public void postData(final Location location){

        // Instantiate the RequestQueue.
        requestQueue = Volley.newRequestQueue(context);

        StringsCommon stringCommon = new StringsCommon();
        String url = stringCommon.postUrl;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", location.username);
                params.put("email", location.email);
                params.put("lat", String.valueOf(location.lat));
                params.put("lng", String.valueOf(location.lng));

                return params;
            }

        };

        requestQueue.add(stringRequest);
    }


    public void getAllLocations(final InterfacePositions<ArrayList<Location>> listener){

        // Instantiate the RequestQueue.
        requestQueue = Volley.newRequestQueue(context);

       // get URL
        StringsCommon stringCommon = new StringsCommon();
        String url = stringCommon.getAllLocations;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                       // textView.setText("Response is: "+ response.substring(0,500));

                        ArrayList<Location> arrLocations = new ArrayList<Location>();


                        // parsing json
                        JSONObject jsonObject = null;
                        JSONArray jsonArray = null;
                        try {
                           // jsonObject = new JSONObject(response);
                            jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo = jsonArray.getJSONObject(i);

                                Location loc = new Location();
                                loc.username = (String) jo.get("username");
                                loc.email = (String) jo.get("email");
                                loc.lat = Float.valueOf(String.valueOf(jo.get("lat")));
                                loc.lng =  Float.valueOf(String.valueOf(jo.get("lng")));
                                arrLocations.add(loc);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        // Log.d(TAG + ": ", "somePostRequest Response : " + response.toString());
                        if(null != response.toString())
                            listener.getResult(arrLocations);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

   

}
