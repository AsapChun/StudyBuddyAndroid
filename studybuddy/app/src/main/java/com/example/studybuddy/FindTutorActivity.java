package com.example.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class FindTutorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "Add Appointment";

    private Button btnConfirm;
    private Spinner AvailableTutorDropDown;
    private Spinner AvailableTimesDropDown;
    private Spinner AvailableDatesDropDown;
    private Spinner LocationsDropDown;
    private static final ArrayList<String> tutorpaths = new ArrayList<String>(Arrays.asList("Pietro")); //get available tutors from firebase
    private static final ArrayList<String> timespaths = new ArrayList<String>(Arrays.asList( "12:00", "13:00", "14:00", "15:00", "16:00", "17:00")); //get available tutors from firebase
    private static final ArrayList<String> datespaths = new ArrayList<>(Arrays.asList( "4/20/2020", "5/20/2020"));
    private static final ArrayList<String> locationspaths = new ArrayList<>(Arrays.asList("Questrom", "GSU", "Law"));
    private String tutor;
    private String date;
    private String tim;
    private String loc;
    private String tutorCourse;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findtutor);
        retrieveSharedPreferenceInfo();

        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        btnConfirm = (Button) findViewById(R.id.btnCreate);

        AvailableTutorDropDown = (Spinner) findViewById(R.id.spinAvailableTutors);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, tutorpaths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AvailableTutorDropDown.setAdapter(adapter);
        AvailableTutorDropDown.setOnItemSelectedListener(this);

        AvailableDatesDropDown = (Spinner) findViewById(R.id.spinDates);
        ArrayAdapter<String> dates = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, datespaths);
        dates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AvailableDatesDropDown.setAdapter(dates);
        AvailableDatesDropDown.setOnItemSelectedListener(this);

        AvailableTimesDropDown = (Spinner) findViewById(R.id.spinAvailableTimes);
        ArrayAdapter<String> times = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, timespaths);
        times.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AvailableTimesDropDown.setAdapter(times);
        AvailableTimesDropDown.setOnItemSelectedListener(this);

        LocationsDropDown = (Spinner) findViewById(R.id.spinLocations);
        ArrayAdapter<String> locations = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, locationspaths);
        locations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LocationsDropDown.setAdapter(locations);
        LocationsDropDown.setOnItemSelectedListener(this);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appointment appoint = new Appointment(tutorCourse, tutor, loc, date, tim );
                addAppointment(appoint);
                goToHomePage();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        if(parent.getId() == R.id.spinAvailableTutors){
            switch (position) {
                case 0:
                    tutor = "Pietro";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
            }
        }
        if(parent.getId() == R.id.spinAvailableTimes){
            switch (position) {
                case 0:
                    tim= "12:00";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 1:
                    tim= "13:00";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 2:
                    tim= "14:00";
                    break;
                case 3:
                    tim= "15:00" ;
                    break;
                case 4:
                    tim= "16:00";
                    break;
                case 5:
                    tim= "17:00";
                    break;
            }
        }
        if(parent.getId() == R.id.spinLocations){
            switch (position) {
                case 0:
                    loc = "Questrom";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";

                    break;
                case 1:
                    loc = "GSU";
                    // Whate]
                    // er you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 2:
                    loc = "Law";
                    break;
            }
        }
        if(parent.getId() == R.id.spinDates){
            switch (position) {
                case 0:
                    date = "4/20/2020";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";

                    break;
                case 1:
                    date = "5/20/2020";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
     //   gender = null;

    }
    void retrieveSharedPreferenceInfo(){
        SharedPreferences simpleAppInfo = getSharedPreferences("ManageAccountActivity", Context.MODE_PRIVATE);

        tutorCourse = simpleAppInfo.getString("tutor", "<missing>");

    }

    private void addAppointment(Appointment app) {

        DocumentReference ProfileRef = db.collection("Profile").document(mAuth.getCurrentUser().getUid());
        ArrayList<String> appoint = new ArrayList<>();
        appoint.add(app.getCourse());
        appoint.add(app.getTutor());
        appoint.add(app.getLocation());
        appoint.add(app.getDate());
        appoint.add(app.getTime());

        ProfileRef
                .update("tutor_session", appoint)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Appointment has been added!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Appointment Error!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

    public void goToHomePage(){
        Intent newIntent = new Intent(this, HomePageActivity.class);
        this.startActivity(newIntent);
    }

}
