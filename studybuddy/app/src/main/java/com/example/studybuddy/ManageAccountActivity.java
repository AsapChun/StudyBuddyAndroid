package com.example.studybuddy;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ManageAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddCourses";

    private Spinner coursesDropDown;
    private Spinner TutorCoursesDropDown;
    private Spinner TutorDropDown;
    private Button btnAddCourse;
    private Button btnAddTutorCourse;
    private Button btnAddTutor;
    private Button btnBack;
    private static final ArrayList<String> paths = new ArrayList<String>(Arrays.asList("cs131", "cs132"));

    private String courseToAdd;
    private String TutorCourseAdd;
    private String findTutoradd;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageaccount);

        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        btnBack = (Button) findViewById(R.id.btnRETURN);
        btnAddCourse = (Button) findViewById(R.id.btnAddCourse);
        btnAddTutorCourse = (Button) findViewById(R.id.btnAddTutorCourse);
        btnAddTutor = (Button) findViewById(R.id.btnAddTutor);
        btnAddCourse= (Button) findViewById(R.id.btnAddCourse);

        coursesDropDown = (Spinner) findViewById(R.id.spinCourses);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManageAccountActivity.this, android.R.layout.simple_spinner_dropdown_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesDropDown.setAdapter(adapter);
        coursesDropDown.setOnItemSelectedListener(this);

        TutorCoursesDropDown = (Spinner) findViewById(R.id.spinTutorSubjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TutorCoursesDropDown.setAdapter(adapter);
        TutorCoursesDropDown.setOnItemSelectedListener(this);

        TutorDropDown = (Spinner) findViewById(R.id.spinFindTutors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TutorDropDown.setAdapter(adapter);
        TutorDropDown.setOnItemSelectedListener(this);

        btnAddCourse.setOnClickListener(new View.OnClickListener() { //add course to firebase
            @Override
            public void onClick(View v) {


            }
        });

        btnAddTutor.setOnClickListener(new View.OnClickListener() { //add course to tutor courses to firebase
            @Override
            public void onClick(View v) {

            }
        });
        btnAddTutorCourse.setOnClickListener(new View.OnClickListener() { //to new activty where we match to a tutor
            @Override
            public void onClick(View v) {

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(v);
            }
        });



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if(parent.getId() == R.id.spinCourses){
            switch (position) {
                case 0:
                    courseToAdd = "cs131";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 1:
                    courseToAdd = "cs132";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
            }
        }
        if(parent.getId() == R.id.spinTutorSubjects){
            switch (position) {
                case 0:
                   TutorCourseAdd = "cs131";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 1:
                    TutorCourseAdd = "cs132";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
            }
        }
        if(parent.getId() == R.id.spinFindTutors){
            switch (position) {
                case 0:
                    findTutoradd = "cs131";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 1:
                    findTutoradd = "cs132";
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
/*
    private void addCourses(String course) {
     //   Log.d(TAG, "createAccount:" + email);

        mAuth.getCurrentUser().updateProfile(course).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    Map<String, Object> Courses = new HashMap<>();
                    Courses.put("Courses", courseToAdd);
                    Courses.put("Tutor Courses", TutorCourseAdd);
                    Courses.put("UpComing Tutor Appointments", findTutoradd);

                    db.collection("Profile").document(user.getUid()).set(Courses)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Profile successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing profile", e);
                                }
                            });

                }
                Toast.makeText(getApplicationContext(), "Courses Add Success.",
                        Toast.LENGTH_SHORT).show();

                } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "Unable to add Courses", task.getException());
                Toast.makeText(getApplicationContext(), "Course Add failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

 */




    public void goBack(View v) {
        this.finish();
    }

}
