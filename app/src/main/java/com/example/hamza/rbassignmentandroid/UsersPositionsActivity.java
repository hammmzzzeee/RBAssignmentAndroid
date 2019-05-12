package com.example.hamza.rbassignmentandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;

import java.util.ArrayList;

public class UsersPositionsActivity extends AppCompatActivity {


   ArrayList<Location> arrLocations;
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_positions);

        // loading
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);


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
                    setListView();


                }
            }
        });




    }

    public void setListView(){
        ListView listView = findViewById(R.id.listView);

        UsersPositionsAdapter adapter = new UsersPositionsAdapter(arrLocations, this);

        listView.setAdapter(adapter);

        // loading finished
        spinner.setVisibility(View.GONE);

        // Clicks on list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location selectedLoc = (Location) arrLocations.get(position);

                Intent intent = new Intent(UsersPositionsActivity.this, UsersActivity.class);
                intent.putExtra("lat", selectedLoc.lat);
                intent.putExtra("lng", selectedLoc.lng);


                startActivity(intent);
            }
        });

    }
}
