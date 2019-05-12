package com.example.hamza.rbassignmentandroid;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class UsersPositionsAdapter extends BaseAdapter {


    ArrayList<Location> arrLocations;
    Context context;
    LayoutInflater layoutInflater;

    public UsersPositionsAdapter(ArrayList<Location> arrLocations, Context context) {
        super();
        this.arrLocations = arrLocations;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return arrLocations.size();
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        convertView= layoutInflater.inflate(R.layout.activity_users_positions_cell, null);


        TextView lblName = convertView.findViewById(R.id.lblName);
        TextView lblLatitude = convertView.findViewById(R.id.lblLatitude);
        TextView lblLongitude = convertView.findViewById(R.id.lblLongitude);

        Location location = arrLocations.get(position);


        lblName.setText(location.username);
        lblLatitude.setText("Latitude: " + String.valueOf(location.lat));
        lblLongitude.setText("Longitude: " + String.valueOf(location.lng));



        return convertView;
    }

}
