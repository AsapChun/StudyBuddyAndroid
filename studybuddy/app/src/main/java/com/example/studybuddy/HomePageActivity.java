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
    private ArrayList<String> appointments;
    private ArrayList<String> studentNames;
    private Map<String, Object> profile;
    private TextView txtSessions;
    private TextView displayCourses;
    private boolean update=true;
    private boolean checkname=true;
    private ProgressDialog progress;

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
        appointments = new ArrayList<>();
        studentNames = new ArrayList<>();

        //retrieve data from bundle, and store them in variables to be used later
        //and retrieve text to display courses and appointments in this activity.
        Bundle b = getIntent().getExtras();
        if(b!=null){
            for(String key: b.keySet()){
                if(key.equals("update")){
                    update = b.getBoolean("update");

                }
                else if(key.equals("classes")){
                    classes = b.getStringArrayList("classes");
                    String classDisplay="";
                    for(String Class: classes){
                        classDisplay+=Class+"<br>";
                    }
                    displayCourses.setText(Html.fromHtml(classDisplay));
                }
                else if(key.equals("appointments")){
                    appointments = b.getStringArrayList("appointments");
                    String appointmentDisplay ="";
                    for(String apt: appointments){
                        appointmentDisplay+=apt+"<br>";
                    }
                    txtSessions.setText(Html.fromHtml(appointmentDisplay));

                }
                else if(key.equals("studentNames")){
                    studentNames = b.getStringArrayList("studentNames");
                }
                else if(key.equals("checkname")){
                    checkname = b.getBoolean("checkname");
                }
            }
        }

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
                    if(checkname){
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

                        //List<String> cities = new ArrayList<>();
                        int counterS=0;
                        for (QueryDocumentSnapshot doc : value) {
                            String class_d = (String) doc.getData().get("ClassName");
                            String location_d = (String) doc.getData().get("Location");
                            ArrayList<String> day_d = (ArrayList<String>) doc.getData().get("Date");
                            String student_d = (String) doc.getData().get("StudentId");
                            Boolean valid = (Boolean) doc.getData().get("validAppointment");
                            Log.d(TAG, doc.getId() + " appointment at " + location_d);
                            if(class_d!=null &&valid ){

                                //check if student name need to be retrieve from database
                                if(checkname){
                                    getStudentName(student_d);
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
                                if(!checkname){
                                    String s = "You have a " + "<i>"+class_d+"</i>" + " appointment with " + "<b>"+studentNames.get(counterS)
                                            +"</b>"+
                                            " at " + location_d + " on " + days+"\n";
                                    appointments.add(s);

                                    String appointmentDisplay ="";
                                    for(String apt: appointments){
                                        appointmentDisplay+=apt+"<br>";
                                    }
                                    txtSessions.setText(Html.fromHtml(appointmentDisplay));
                                    progress.dismiss();
                                }

                            }
                            counterS+=1;
                        }

                    }
                });


    }
    private void getStudentName(String id){

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
                        studentNames.add(fname+" "+lname);
                    }
                    Intent i = new Intent(getBaseContext(), HomePageActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean("update",true);
                    b.putBoolean("checkname",false);
                    b.putStringArrayList("classes",classes);
                    b.putStringArrayList("appointments",appointments);
                    b.putStringArrayList("studentNames",studentNames);
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
        if ( progress!=null && progress.isShowing() ){
            progress.cancel();
        }
    }

}

