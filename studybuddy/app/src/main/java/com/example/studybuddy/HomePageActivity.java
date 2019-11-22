package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity {
    private static final String TAG = "HomePage";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase;
    private ArrayList<String> your_class;
    private Map<String, Object> profile;
    private TextView txtSessions;
    private TextView displayCourses;

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
        DocumentReference docRef = db.collection("Profile").document(mAuth.getCurrentUser().getUid());



       docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                              @Override
                                              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                  if (task.isSuccessful()) {
                                                      DocumentSnapshot document = task.getResult();
                                                      if (document != null) {
                                                          Map<String, Object> data = document.getData();
                                                          Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                          ArrayList<String> sessions = (ArrayList<String>) data.get("tutor_session");
                                                          if(sessions != null){
                                                              String s = "You have a " + sessions.get(0) + " appointment with " + sessions.get(1) +
                                                                      " at " + sessions.get(2) + " on " + sessions.get(3) + " at " + sessions.get(4);
                                                              txtSessions.setText(s);
                                                          }


                                                          ArrayList<String> courses = (ArrayList<String>) data.get("your_class");
                                                          if(courses !=  null){
                                                              Iterator c = courses.iterator();
                                                              String cc  = "";
                                                              while(c.hasNext()){
                                                                  String toAddClass = c.next().toString();
                                                                  cc = cc + "Course: " + toAddClass + "\n";
                                                              }
                                                              displayCourses.setText(cc);
                                                          }
                                                      }
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
        this.startActivity(newIntent);
    }
    public void goToManageAccount(){
        Intent newIntent = new Intent(this, ManageAccountActivity.class);
        this.startActivity(newIntent);
    }

    public void signOut(){

        try {
            mAuth.signOut();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

