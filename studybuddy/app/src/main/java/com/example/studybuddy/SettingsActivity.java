package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Button btnBack;
    private Button btnChangeEmail;
    private Button btnChangePassword;
    private TextView OldUsername;
    private EditText NewUsername;
    private TextView OldPassword;
    private EditText NewPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack =  (Button) findViewById(R.id.btnNewBack);
        btnChangeEmail = (Button) findViewById(R.id.btnChangeEmail);
        btnChangePassword = (Button) findViewById(R.id.btnConfirm);
        OldUsername = (TextView) findViewById(R.id.edtOldUsername);
        NewUsername = (EditText) findViewById(R.id.edtNewUsername);
        OldPassword = (TextView) findViewById(R.id.edtOldPassword);
        NewPassword = (EditText) findViewById(R.id.edtNewPassword);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goBack();
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
    public void goBack() {

        this.finish();
    }





}
