package com.example.studybuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


//doesnt actually create an appointment but rather creates the necessary fields to set up an appoitnemnt



public class CreateAppointment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "Create Tutor";
    private String tutorableSubject;
    private TextView tutorInfo;
    private TextView txtDaysChoosen;
    private Button btnCancel;
    private Button btnCreateTutor;
    private Button btnSelectDay;
    private Spinner dropDownLocations;
    private Spinner dropDownDays;

    private EditText tutorRate;

    private ArrayList<String> daysAvailable = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final ArrayList<String> loca = new ArrayList<String>(Arrays.asList("GSU", "EMA", "Questrom"));
    private static final ArrayList<String> days = new ArrayList<String>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
    private String location;
    private String rate;
    private String dayyy;
    private String UserId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createappointment);
        retrieveSharedPreferenceInfo();

        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Profile").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Map<String, Object> data = document.getData();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String id = (String) data.get("user_id");
                        UserId = id; //get UserId from Profile in firebase
                    }
                }
            }

        });

        tutorInfo = (TextView) findViewById(R.id.txtTutorInformation);
        txtDaysChoosen = (TextView) findViewById(R.id.txtDaysChoosen);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCreateTutor = (Button) findViewById(R.id.btnCreateTutorAppt);
        btnSelectDay = (Button) findViewById(R.id.btnSelectDays);
        tutorRate = (EditText) findViewById(R.id.edtPrice);

        tutorInfo.setText(tutorInfo.getText().toString() + " " + tutorableSubject);

        dropDownLocations = (Spinner) findViewById(R.id.spinLocation);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateAppointment.this, android.R.layout.simple_spinner_dropdown_item, loca);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDownLocations.setAdapter(adapter);
        dropDownLocations.setOnItemSelectedListener(this);


        dropDownDays = (Spinner) findViewById(R.id.spinDaTes);
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<String>(CreateAppointment.this, android.R.layout.simple_spinner_dropdown_item, days);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDownDays.setAdapter(daysAdapter);
        dropDownDays.setOnItemSelectedListener(this);

        btnSelectDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(daysAvailable.contains(dayyy)){
                    Toast.makeText(getApplicationContext(), "This day has already been added!", Toast.LENGTH_LONG).show();
                } else {
                    // add day to array list for firebase storage
                    daysAvailable.add(dayyy);
                    //update the textview
                    txtDaysChoosen.setText(txtDaysChoosen.getText().toString() + " " + dayyy);
                }
            }
        });

        btnCreateTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = tutorRate.getText().toString();
                createTutor(tutorableSubject, location, daysAvailable, rate);
               // Toast.makeText(getApplicationContext(), "Successful!", Toast.LENGTH_LONG).show();
                goBack(v);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(v);
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if (parent.getId() == R.id.spinDaTes) {
            switch (position) {
                case 0:
                    dayyy = "Monday";
                    break;
                case 1:
                    dayyy = "Tuesday";
                    break;
                case 2:
                    dayyy = "Wednesday";
                    break;
                case 3:
                    dayyy = "Thursday";
                    break;
                case 4:
                    dayyy = "Friday";
                    break;
                case 5:
                    dayyy = "Saturday";
                    break;
                case 6:
                    dayyy = "Sunday";
                    break;
            }
        }

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


    void retrieveSharedPreferenceInfo(){
        SharedPreferences simpleAppInfo = getSharedPreferences("ManageAccountActivity", Context.MODE_PRIVATE);

        tutorableSubject = simpleAppInfo.getString("tutorCourse", "<missing>");


    }

    //TODO

    // location;
    //daysAvailable;
    // tutorableSubject;

    private boolean validateForm() {
        boolean valid = true;

        if (location == null) {
            valid = false;
        }
        if(daysAvailable.isEmpty()){
            valid = false;
        }
        if(rate == null){
            valid = false;
        }


        return valid;
    }

    //rate = tutorRate.getText().toString();
    // location;
    //daysAvailable;
    // tutorableSubject;

    private void createTutor(String subject, String loc, ArrayList<String> day , String price) {
        Log.d(TAG, "createTutor");
        Map<String, Object> appointment= new HashMap<>();
        appointment.put("ClassName", subject);
        appointment.put("validAppointment", false);
        appointment.put("Location", loc);
        appointment.put("price", price);
        appointment.put("TutorId", mAuth.getCurrentUser().getUid());
        appointment.put("Date", day);
        appointment.put("StudentId", null);
        appointment.put("rated", false);

        db.collection("Appointment").document()
                .set(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    public void goBack(View v) {
        this.finish();
    }

}
