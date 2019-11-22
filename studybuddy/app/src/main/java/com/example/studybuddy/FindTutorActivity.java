package com.example.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class FindTutorActivity extends AppCompatActivity {
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
    private TextView txtFindTut;

    private ListView lvTutors;
    private ListAdapter lvAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findtutor);
        retrieveSharedPreferenceInfo();

        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        txtFindTut = (TextView) findViewById(R.id.txtTutorClassInfo);

        txtFindTut.setText(txtFindTut.getText().toString() + " " + tutorCourse);






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
/*

class MyCustomAdapter extends BaseAdapter {
    private ArrayList<String> tutors = new ArrayList<>(); //tutor names
    private ArrayList<String> price = new ArrayList<>();
    private ArrayList<String> ` `
    private Float ratings[];       //keep track of all ratings
    private Context context;

    public MyCustomAdapter(Context acontext){
        context = acontext;
      //  tutors = ; //pull all available tutors from firebase
      //  price = ; // pull all prices of tutors (para
    }
}

 */


