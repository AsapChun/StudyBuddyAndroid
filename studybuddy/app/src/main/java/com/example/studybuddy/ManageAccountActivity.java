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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
    private static final ArrayList<String> paths = new ArrayList<String>(Arrays.asList("cs111", "cs112", "cs131", "cs132","cs235","cs237", "cs320", "cs330", "cs350"));
    private ArrayList<String> courses= new ArrayList<>();
    private ArrayList<String> Tcourses= new ArrayList<>();
    private ArrayList<String> findTutorCourses = new ArrayList<>(); // dynamically update this list from firebase to show courses user can be tutored in
    private String courseToAdd;
    private String TutorCourseAdd;
    private String findTutoradd;
    private String test;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageaccount);

        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

    /*    DocumentReference docRef = db.collection("Profile").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        Map<String, Object> data = document.getData();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ArrayList<String> courses = (ArrayList<String>) data.get("your_class");
                        if(courses != null){
                            Iterator cc = courses.iterator();
                            while(cc.hasNext()){
                                findTutorCourses.add(cc.next().toString());
                            }
                        }
                    }
                }
            }
        });

     */

        btnBack = (Button) findViewById(R.id.btnRETURN);
//        btnAddCourse = (Button) findViewById(R.id.btnAddCourse);
        btnAddTutorCourse = (Button) findViewById(R.id.btnAddTutorCourse);
        btnAddTutor = (Button) findViewById(R.id.btnAddTutor);
//        btnAddCourse= (Button) findViewById(R.id.btnAddCourse);

//        coursesDropDown = (Spinner) findViewById(R.id.spinCourses);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManageAccountActivity.this, R.layout.spinner_item, paths);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        coursesDropDown.setAdapter(adapter);
//        coursesDropDown.setOnItemSelectedListener(this);

        TutorCoursesDropDown = (Spinner) findViewById(R.id.spinTutorSubjects);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        TutorCoursesDropDown.setAdapter(adapter);
        TutorCoursesDropDown.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapt = new ArrayAdapter<String>(ManageAccountActivity.this, R.layout.spinner_item, paths);
        TutorDropDown = (Spinner) findViewById(R.id.spinFindTutors);
        adapt.setDropDownViewResource(R.layout.spinner_dropdown_item);
        TutorDropDown.setAdapter(adapt);
        TutorDropDown.setOnItemSelectedListener(this);

//        btnAddCourse.setOnClickListener(new View.OnClickListener() { //add course to firebase
//            @Override
//            public void onClick(View v) {
//             if(courses.isEmpty()) {
//                 courses.add(courseToAdd);
//                 addCourses(courses);
//             } else{
//                 if(courses.contains(courseToAdd)){
//                     Toast.makeText(getApplicationContext(), "Course has already been added!", Toast.LENGTH_SHORT).show();
//                 }
//                 else{
//                     courses.add(courseToAdd);
//                     addCourses(courses);
//                 }
//             }
//
//            }
//        });

        btnAddTutorCourse.setOnClickListener(new View.OnClickListener() { //add course to tutor courses to firebase
            @Override
            public void onClick(View v) {
                /*
                if(Tcourses.isEmpty()) {
                    Tcourses.add(TutorCourseAdd);
                    addTutorCourses(Tcourses);
                } else{
                    if(Tcourses.contains(TutorCourseAdd)){ //checks to make sure isnt added twice
                        Toast.makeText(getApplicationContext(), "Tutor subject has already been added!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Tcourses.add(TutorCourseAdd);
                        addTutorCourses(Tcourses);
                    }
                }

                 */
                saveSharedPreferenceInfo();
                goToCreateTutor();

            }
        });

        btnAddTutor.setOnClickListener(new View.OnClickListener() { //to new activty where we match to a tutor
            @Override
            public void onClick(View v) {
                saveSharedPreferenceInfo();
                goToAddTutor();
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
//        if(parent.getId() == R.id.spinCourses){
//            switch (position) {
//                case 0:
//                    courseToAdd = "cs111";
//                    break;
//                case 1:
//                    courseToAdd = "cs112";
//                    break;
//                case 2:
//                    courseToAdd = "cs131";
//                    break;
//                case 3:
//                    courseToAdd = "cs132";
//                    break;
//                case 4:
//                    courseToAdd = "cs235";
//                    break;
//                case 5:
//                    courseToAdd = "cs237";
//                    break;
//                case 6:
//                    courseToAdd = "cs320";
//                    break;
//                case 7:
//                    courseToAdd = "cs330";
//                    break;
//                case 8:
//                    courseToAdd = "cs350";
//                    break;
//            }
//        }
        if(parent.getId() == R.id.spinTutorSubjects){
            switch (position) {
                case 0:
                   TutorCourseAdd = "cs111";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 1:
                    TutorCourseAdd = "cs112";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 2:
                    TutorCourseAdd = "cs131";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 3:
                    TutorCourseAdd = "cs132";
                    break;
                case 4:
                    TutorCourseAdd = "cs235";
                    break;
                case 5:
                    TutorCourseAdd = "cs237";
                    break;
                case 6:
                    TutorCourseAdd = "cs320";
                    break;
                case 7:
                    TutorCourseAdd = "cs330";
                    break;
                case 8:
                    TutorCourseAdd = "cs350";
                    break;
            }
        }
        if(parent.getId() == R.id.spinFindTutors) {
            switch (position) {
                case 0:
                    findTutoradd = "cs111";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 1:
                    findTutoradd = "cs112";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 2:
                    findTutoradd = "cs131";
                    // Whatever you want to happen when the first item gets selected
                    //  gender = "Male";
                    break;
                case 3:
                    findTutoradd = "cs132";
                    break;
                case 4:
                    findTutoradd = "cs235";
                    break;
                case 5:
                    findTutoradd = "cs237";
                    break;
                case 6:
                    findTutoradd = "cs320";
                    break;
                case 7:
                    findTutoradd = "cs330";
                    break;
                case 8:
                    findTutoradd = "cs350";
                    break;
            }
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
     //   gender = null;

    }

    private void addCourses(ArrayList course) {

        DocumentReference ProfileRef = db.collection("Profile").document(mAuth.getCurrentUser().getUid());

        ProfileRef
                .update("your_class", course)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Course has been added!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

    private void addTutorCourses(ArrayList Tcourse) {

        DocumentReference ProfileRef = db.collection("Profile").document(mAuth.getCurrentUser().getUid());

        ProfileRef
                .update("tutor_class", Tcourse)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Tutor subject has been added!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }


    void saveSharedPreferenceInfo(){
        //1. Refer to the SharedPreference Object.
        SharedPreferences simpleAppInfo = getSharedPreferences("ManageAccountActivity", Context.MODE_PRIVATE);  //Private means no other Apps can access this.

        //2. Create an Shared Preferences Editor for Editing Shared Preferences.
        //Note, not a real editor, just an object that allows editing...

        SharedPreferences.Editor editor = simpleAppInfo.edit();

        //3. Store what's important!  Key Value Pair, what else is new...
        editor.putString("tutor", findTutoradd);
        editor.putString("tutorCourse", TutorCourseAdd);

        //4. Save your information.
        editor.apply();

        Toast.makeText(this, "Shared Preference Data Updated.", Toast.LENGTH_LONG).show();
    }





    public void goToAddTutor(){
        Intent newIntent = new Intent(this, FindTutorActivity.class);
        this.startActivity(newIntent);
    }
    public void goToCreateTutor(){
        Intent newIntent = new Intent(this, CreateAppointment.class);
        this.startActivity(newIntent);
    }

    public void goBack(View v) {
        Intent intent = new Intent(getBaseContext(), HomePageActivity.class);
        this.finish();
        startActivity(intent);
    }

}
