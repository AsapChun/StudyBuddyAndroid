package com.example.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.Model.Appointment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class ManageReservationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView txtHeader;
    private TextView txtCurrentDay;
    private TextView txtCurrentLoc;
    private TextView txtChangeDayInfo;
    private static final String TAG = "Update Appointment";

    private CheckBox cbMonday;
    private CheckBox cbTuesday;
    private CheckBox cbWednesday;
    private CheckBox cbThursday;
    private CheckBox cbFriday;
    private CheckBox cbSaturday;
    private CheckBox cbSunday;

    private Button btnUpdate;
    private Button btnUpdateLoc;
    private Button btnCancelReserve;
    private Button btnConfirmDay;

    private Spinner spinnerDay;
    private Spinner spinnerLocation;

    private String day;
    private ArrayList<String> schedule = new ArrayList<>();
    private String location;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String appID;

    private static final ArrayList<String> loca = new ArrayList<String>(Arrays.asList("GSU", "EMA", "Questrom"));
    private static final ArrayList<String> days = new ArrayList<String>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));

    private String appointment;

    private Appointment app;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservemanage);
        Intent intent = getIntent();
        app = (Appointment) intent.getSerializableExtra("Appointment");
        appID = app.getAppId();

        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        txtHeader = (TextView) findViewById(R.id.txtHeader);
        txtCurrentDay = (TextView) findViewById(R.id.txtCurrentDay);
        txtCurrentLoc = (TextView) findViewById(R.id.txtCurrentLoc);
        txtChangeDayInfo = (TextView) findViewById(R.id.txtChangeDayInfo);
      //  btnUpdate = (Button) findViewById(R.id.btnUpdate); //update day of reservatuon
        btnCancelReserve = (Button) findViewById(R.id.btnCancelReserve); //cancel reservation
        btnUpdateLoc = (Button) findViewById(R.id.btnUpdateLoc);//update location of reservation
        btnConfirmDay = (Button) findViewById(R.id.btnConfirmDay); //confirm location update

        cbMonday = (CheckBox) findViewById(R.id.cbMonday);
        cbTuesday = (CheckBox) findViewById(R.id.cbTuesday);
        cbWednesday = (CheckBox) findViewById(R.id.cbWednesday);
        cbThursday = (CheckBox) findViewById(R.id.cbThursday);
        cbFriday = (CheckBox) findViewById(R.id.cbFriday);
        cbSaturday = (CheckBox) findViewById(R.id.cbSaturday);
        cbSunday = (CheckBox) findViewById(R.id.cbSunday);

        //txtHeader.setText("Modify Reservation with " + app.getTutor());
        txtCurrentDay.setText(Html.fromHtml("Current appointment's day: " + "<b>" +app.getDate()+"</b>"));
        txtCurrentLoc.setText(Html.fromHtml("Current location: " + "<b>" + app.getLocation() + "</b>"));


        spinnerLocation = (Spinner) findViewById(R.id.spinLocation);
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(ManageReservationActivity.this, android.R.layout.simple_spinner_dropdown_item, loca);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapt);
        spinnerLocation.setOnItemSelectedListener(this);



        btnConfirmDay.setOnClickListener(new View.OnClickListener() { //update day of reservation
            @Override
            public void onClick(View v) {
                if(cbMonday.isChecked()){
                    schedule.add("Monday");
                }
                if(cbTuesday.isChecked()){
                    schedule.add("Tuesday");
                }
                if(cbWednesday.isChecked()){
                    schedule.add("Wednesday");
                }
                if(cbThursday.isChecked()){
                    schedule.add("Thursday");
                }
                if(cbFriday.isChecked()){
                    schedule.add("Friday");
                }
                if(cbSaturday.isChecked()){
                    schedule.add("Saturday");
                }
                if(cbSunday.isChecked()){
                    schedule.add("Sunday");
                }

                if(schedule.size() != 0){
                    DocumentReference AppointRef = db.collection("Appointment").document(appID);//insert appointment id

                    // Set the "isCapital" field of the city 'DC'
                    AppointRef
                            .update("Date", schedule)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });

                }
              goToHomePage();
            }

        });

        btnCancelReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Appointment").document(appID)//insert appointment id
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
                goToHomePage();
            }
        });

        btnUpdateLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference AppointRef = db.collection("Appointment").document(appID);//insert appointment id

                AppointRef
                        .update("Location", location)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });

                goToHomePage();
            }
        });
    }

    void retrieveSharedPreferenceInfo(){
        SharedPreferences AppInfo = getSharedPreferences("HomePageActivity", Context.MODE_PRIVATE);
        appointment = AppInfo.getString("appoint", "<missing>");
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        if (parent.getId() == R.id.spinLocation) {
            switch (position) {
                case 0:
                    location = "GSU";
                    break;
                case 1:
                    location = "EMA";
                    break;
                case 2:
                    location = "Questrom";
                    break;
            }
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
