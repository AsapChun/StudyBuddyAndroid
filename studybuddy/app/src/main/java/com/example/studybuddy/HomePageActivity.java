package com.example.studybuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.geojson.Point;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity {
    private static final String TAG = "HomePage";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase;
    private ArrayList<String> classes;
    private ArrayList<String> tutorAppointments;
    private ArrayList<String> studentAppointments;
    private ArrayList<String> Names;
    private ArrayList<String> studentNames;
    private ArrayList<String> tutorNames;
    private Map<String, Object> profile;
    private TextView txtSessions;
    private TextView displayCourses;
    private boolean update=true;
    private boolean studentupdate=true;
    private boolean tutorupdate=false;
    private boolean checkname=true;
    private boolean checknameagain=true;
    private ProgressDialog progress;
    private boolean checkclass=true;    //boolean check if class need to be updated


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        txtSessions = (TextView) findViewById(R.id.txtTutorSessions);
        txtSessions.setMovementMethod(new ScrollingMovementMethod());
        displayCourses = (TextView) findViewById(R.id.txtCourses);
        displayCourses.setMovementMethod(new ScrollingMovementMethod());

        //add loading screen
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please Wait while Loading Data...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog


        classes = new ArrayList<>();
        tutorAppointments = new ArrayList<>();
        studentAppointments = new ArrayList<>();
        Names = new ArrayList<>();
        studentNames = new ArrayList<>();
        tutorNames = new ArrayList<>();


        //retrieve data from bundle, and store them in variables to be used later
        //and retrieve text to display courses and appointments in this activity.
        Bundle b = getIntent().getExtras();
        if(b!=null){
            for(String key: b.keySet()){
                if(key.equals("update")){
                    update = b.getBoolean("update");

                }
                else if(key.equals("studentupdate")){
                    studentupdate = b.getBoolean("studentupdate");
                }
                else if(key.equals("tutorupdate")){
                    tutorupdate = b.getBoolean("tutorupdate");
                }
                else if(key.equals("classes")){
                    classes = b.getStringArrayList("classes");
                    String classDisplay="";
                    for(String Class: classes){
                        classDisplay+=Class+"<br>";
                    }
                    displayCourses.setText(Html.fromHtml(classDisplay));
                }
                else if(key.equals("studentAppointments")){
                    studentAppointments = b.getStringArrayList("studentAppointments");

                }
                else if (key.equals("tutorAppointments")){
                    tutorAppointments = b.getStringArrayList("tutorAppointments");
                }
                else if(key.equals("studentNames")){
                    studentNames = b.getStringArrayList("studentNames");
                }
                else if(key.equals("tutorNames")){
                    tutorNames = b.getStringArrayList("tutorNames");
                }
                else if(key.equals("checkname")){
                    checkname = b.getBoolean("checkname");
                }
                else if(key.equals("checknameagain")){
                    checknameagain = b.getBoolean("checknameagain");
                }
                else if(key.equals("checkclass")){
                    checkclass = b.getBoolean("checkclass");
                }
            }
        }
        String appointmentDisplay ="";
        for(String apt: studentAppointments){
            appointmentDisplay+=apt+"<br>";
        }
        for(String apt: tutorAppointments){
            appointmentDisplay+=apt+"<br>";
        }
        txtSessions.setText(Html.fromHtml(appointmentDisplay));

        //if no appointment updated are needed, finish loading
        if(!studentupdate && !tutorupdate){
            update=false;
            progress.dismiss();
        }

        //check if any update to the homepage is needed
        if(update) {
            progress.show();
            getHomePageData();
        }

    }

    //retrieve data for classes and appointments
    private void getHomePageData(){

        //retrieve class information
        final DocumentReference docRef1 = db.collection("Profile").document(mAuth.getCurrentUser().getUid());
        docRef1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    Map<String, Object> data = snapshot.getData();
                    ArrayList<String> courses = (ArrayList<String>) data.get("your_class");
                    if(checkclass){
                        for(String c: courses){
                            classes.add(c);
                        }
                        String classDisplay="";
                        for(String Class: classes){
                            classDisplay+=Class+"<br>";
                        }
                        displayCourses.setText(Html.fromHtml(classDisplay));
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        //check if appointment for user as student needs to be updated
        if(studentupdate){
            getAppointmentAsStudent();
        }
        //check if appointment for user as tutor needs to be updated
        if(tutorupdate){
            getAppointmentAsTutor();
        }


    }
    //retrieve data for appointments for users as a student
    private void getAppointmentAsStudent(){
        db.collection("Appointment")
                .whereEqualTo("StudentId",mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        int counterS=0;
                        //boolean to check if query is empty
                        boolean empty=true;
                        for (QueryDocumentSnapshot doc : value) {
                            String class_d = (String) doc.getData().get("ClassName");
                            String location_d = (String) doc.getData().get("Location");
                            ArrayList<String> day_d = (ArrayList<String>) doc.getData().get("Date");
                            String student_d = (String) doc.getData().get("TutorId");
                            Boolean valid = (Boolean) doc.getData().get("validAppointment");
                            Log.d(TAG, doc.getId() + " appointment at " + location_d);
                            if(class_d!=null &&valid ){
                                empty=false;
                                //check if student name need to be retrieve from database
                                if(checknameagain){
                                    getStudentName(student_d,true);
                                }

                                //formatt appointment text
                                String days ="";
                                int counter=0;
                                for(String day: day_d){
                                    if(counter==day_d.size()-1){
                                        days+=day;
                                    }else{
                                        days+=day+", ";
                                    }
                                    counter+=1;
                                }

                                //store the text to display each appointment into array
                                if(!checknameagain){
                                    String s = "You have a " + "<i>"+class_d+"</i>" + " appointment with " + "<b>"+tutorNames.get(counterS)
                                            +"</b>"+
                                            " at " + location_d + " on " + days+"\n";
                                    studentAppointments.add(s);

                                }

                            }
                            counterS+=1;
                        }
                        //save the data needed, and restart current activity
                        if(!checknameagain || empty){
                            Intent i = new Intent(getBaseContext(), HomePageActivity.class);
                            Bundle b = new Bundle();
                            b.putBoolean("update",true);
                            b.putBoolean("studentupdate",false);
                            b.putBoolean("checkclass",false);
                            b.putBoolean("tutorupdate",true);

                            b.putStringArrayList("studentNames",studentNames);
                            b.putStringArrayList("tutorNames",tutorNames);
                            b.putStringArrayList("classes",classes);
                            b.putStringArrayList("studentAppointments",studentAppointments);
                            b.putStringArrayList("tutorAppointments",tutorAppointments);
                            i.putExtras(b);
                            goBack();
                            startActivity(i);
                        }
                    }
                });
    }

    //retrieve data for appointments for user as a tutor
    private void getAppointmentAsTutor(){
        //retrieve appointment information
        db.collection("Appointment")
                .whereEqualTo("TutorId",mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        int counterS=0;
                        //boolean to check if query is empty
                        boolean empty=true;
                        for (QueryDocumentSnapshot doc : value) {
                            String class_d = (String) doc.getData().get("ClassName");
                            String location_d = (String) doc.getData().get("Location");
                            ArrayList<String> day_d = (ArrayList<String>) doc.getData().get("Date");
                            String student_d = (String) doc.getData().get("StudentId");
                            Boolean valid = (Boolean) doc.getData().get("validAppointment");
                            Log.d(TAG, doc.getId() + " appointment at " + location_d);
                            if(class_d!=null &&valid ){
                                empty=false;
                                //check if student name need to be retrieve from database
                                if(checkname){
                                    getStudentName(student_d,false);
                                }

                                //formatt appointment text
                                String days ="";
                                int counter=0;
                                for(String day: day_d){
                                    if(counter==day_d.size()-1){
                                        days+=day;
                                    }else{
                                        days+=day+", ";
                                    }
                                    counter+=1;
                                }

                                //store the text to display each appointment into array
                                if(!checkname ){
                                    String s = "You have a " + "<i>"+class_d+"</i>" + " tutoring session with " + "<b>"+studentNames.get(counterS)
                                            +"</b>"+
                                            " at " + location_d + " on " + days+"\n";
                                    tutorAppointments.add(s);

                                }

                            }
                            counterS+=1;
                        }
                        //store the data needed, restart current activity
                        if(!checkname || empty){
                            Intent i = new Intent(getBaseContext(), HomePageActivity.class);
                            Bundle b = new Bundle();
                            b.putBoolean("update",true);
                            b.putBoolean("checkclass",false);
                            b.putBoolean("tutorupdate",false);
                            b.putBoolean("studentupdate",studentupdate);

                            b.putStringArrayList("studentNames",studentNames);
                            b.putStringArrayList("tutorNames",tutorNames);
                            b.putStringArrayList("classes",classes);
                            b.putStringArrayList("studentAppointments",studentAppointments);
                            b.putStringArrayList("tutorAppointments",tutorAppointments);
                            i.putExtras(b);
                            goBack();
                            startActivity(i);
                        }

                    }
                });
    }

    //retrieve names given studnet ID
    private void getStudentName(String id,boolean flag){
        final boolean isStudent=flag;
        DocumentReference docRef = db.collection("Profile").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Map<String, Object> data = document.getData();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String fname = (String) data.get("first_name");
                        String lname = (String) data.get("last_name");
                        if(isStudent==true){
                            tutorNames.add(fname+" "+lname);
                        }
                        if(isStudent==false){
                            studentNames.add(fname+" "+lname);
                        }
                        //Names.add(fname+" "+lname);
                    }
                    //store data needed, and restart current activity
                    Intent i = new Intent(getBaseContext(), HomePageActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean("update",true);
                    if(isStudent==true){
                        b.putBoolean("checknameagain",false);


                    }
                    if(isStudent==false){
                        b.putBoolean("checkname",false);

                    }
                    b.putBoolean("checkclass",false);
                    b.putStringArrayList("studentNames",studentNames);
                    b.putStringArrayList("tutorNames",tutorNames);
                    b.putStringArrayList("classes",classes);
                    b.putBoolean("studentupdate",studentupdate);
                    b.putBoolean("tutorupdate",tutorupdate);
                    b.putStringArrayList("studentAppointments",studentAppointments);
                    b.putStringArrayList("tutorAppointments",tutorAppointments);
                    i.putExtras(b);
                    goBack();
                    startActivity(i);
                }
            }

        });

    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.idManageAccount:
                Toast.makeText(getApplicationContext(), "Manage Account Selected", Toast.LENGTH_SHORT).show();
                goToManageAccount();
                return true;
            case R.id.itmProfile:
                Toast.makeText(getApplicationContext(), "Profile Selected", Toast.LENGTH_SHORT).show();
                goToProfile();
                return true;
            case R.id.itmMessages:
                Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_SHORT).show();
                goToMessages();
                return true;
            case R.id.itmPayment:
                goToPayment();
                return true;
            case R.id.itmLocation:
                goToLocation();
                return true;
            case R.id.itmSettings:
                Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_SHORT).show();
                goToSettings();
                return true;
            case R.id.itmLogoff:
                signOut();
                goBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goBack() {
        this.finish();
    }

    public void goToSettings(){
        Intent newIntent = new Intent(this, SettingsActivity.class);
        this.startActivity(newIntent);
    }
    public void goToPayment(){
        Intent newIntent = new Intent(this, PaymentActivity.class);
        this.startActivity(newIntent);
    }
    public void goToProfile(){
        Intent newIntent = new Intent(this, ProfileActivity.class);
        this.startActivity(newIntent);
    }
    public void goToLocation(){
        Intent newIntent = new Intent(this, LocationActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("update",true);
        newIntent.putExtras(b);
        this.startActivity(newIntent);
    }
    public void goToManageAccount(){
        Intent newIntent = new Intent(this, ManageAccountActivity.class);
        this.startActivity(newIntent);
    }
    public void goToMessages(){
        Intent newIntent = new Intent(this, ChatActivity.class);
        this.startActivity(newIntent);
    }


    public void signOut(){

        try {
            mAuth.signOut();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //press back button on homepage, go back to home screen of the device
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //destroy loading screen if current activity is destroy
        if ( progress!=null && progress.isShowing() ){
            progress.cancel();
        }
    }

}

