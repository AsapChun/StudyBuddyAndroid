package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studybuddy.Model.Appointment;
import com.example.studybuddy.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "Settings Update";

    private Button btnBack;
    private Button btnChangeEmail;
    private Button btnChangePassword;
    private Button btnDelete;
    private EditText OldUsername;
    private EditText NewUsername;
    private EditText OldPassword;
    private EditText NewPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private CheckBox cbConfirm;

    private String usernameCheck;
    private String passwordCheck;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack =  (Button) findViewById(R.id.btnNewBack);
        btnChangeEmail = (Button) findViewById(R.id.btnChangeEmail);
        btnChangePassword = (Button) findViewById(R.id.btnConfirm);
        OldUsername = (EditText) findViewById(R.id.edtEmail);
        NewUsername = (EditText) findViewById(R.id.edtNewUsername);
        OldPassword = (EditText) findViewById(R.id.edtPassword);
        NewPassword = (EditText) findViewById(R.id.edtNewPassword);
        cbConfirm = (CheckBox) findViewById(R.id.cbConfirm);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();



        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //update user login email
                String email = user.getEmail();
                if(OldUsername.getText().toString().equals(email)){
                    user.updateEmail(NewUsername.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User email address updated.");
                                    }
                                }
                            });
                    goBackToHomePage();
                } else{
                    Toast.makeText(getApplicationContext(), "Email does not match current email!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(OldPassword.getText().toString().equals(NewPassword.getText().toString())){
                    user.updatePassword(NewPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User password updated.");
                                    }
                                }
                            });
                    goBackToHomePage();
                    }
                else{
                    Toast.makeText(getApplicationContext(), "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                    }
                }

        });



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToHomePage();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbConfirm.isChecked()){
                    db.collection("Appointment") //delete appointments with user's id
                            .whereEqualTo("StudentId", user.getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //cannot be both tutor and student in a appointment
                                            db.collection("Appointment").document(document.getId())
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
                                                               }
                                                           }
                                                       }
                                                   });
                    db.collection("Appointment") //delete appointments with tutor's id
                            .whereEqualTo("TutorId", user.getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            db.collection("Appointment").document(document.getId())
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
                                        }
                                    }
                                }
                            });
                                        user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User account deleted.");
                                                }
                                            }
                                        });


                                goBackToHomePage();
                        } else{
                          Toast.makeText(getApplicationContext(), "Check Box To Confirm Delete Account!", Toast.LENGTH_SHORT).show();
                 }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Toast.makeText(getApplicationContext(), "You are not signed in ",
                    Toast.LENGTH_SHORT).show();
            MainActivity();

        }

    }

    public void MainActivity(){
        Intent newIntent = new Intent(this, MainActivity.class);
        this.startActivity(newIntent);
    }
    public void goBackToHomePage() {
        Intent newIntent = new Intent(this, HomePageActivity.class);
        this.startActivity(newIntent);
    }
}
