package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class FindTutorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Button btnConfirm;
    private Spinner AvailableTutorDropDown;
    private Spinner AvailableTimesDropDown;
    private Spinner LocationsDropDown;
    private static final ArrayList<String> paths = new ArrayList<String>(Arrays.asList("cs131", "cs132")); //get available tutors from firebase

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findtutor);

        btnConfirm = (Button) findViewById(R.id.btnCreate);

        AvailableTutorDropDown = (Spinner) findViewById(R.id.spinAvailableTutors);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AvailableTutorDropDown.setAdapter(adapter);
        AvailableTutorDropDown.setOnItemSelectedListener(this);

        AvailableTimesDropDown = (Spinner) findViewById(R.id.spinAvailableTimes);
        ArrayAdapter<String> times = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, paths);
        times.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AvailableTimesDropDown.setAdapter(adapter);
        AvailableTimesDropDown.setOnItemSelectedListener(this);

        LocationsDropDown = (Spinner) findViewById(R.id.spinLocations);
        ArrayAdapter<String> locations = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, paths);
        locations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LocationsDropDown.setAdapter(adapter);
        LocationsDropDown.setOnItemSelectedListener(this);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHomePage();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
               // gender = "Male";
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
           //     gender = "Female";
                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
     //   gender = null;

    }

    public void goToHomePage(){
        Intent newIntent = new Intent(this, HomePageActivity.class);
        this.startActivity(newIntent);
    }

}
