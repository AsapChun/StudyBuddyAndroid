package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";
    private Button btnNewUser;
    private Button btnLogin;
    private EditText edtEmail;
    private EditText edtPassword;
    private FirebaseAuth mAuth;
    private Button btnSignOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNewUser = (Button) findViewById(R.id.btnNewUser);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        mAuth = FirebaseAuth.getInstance();
        btnSignOut = (Button) findViewById(R.id.btnSignOut);


        //Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(edtEmail.getText().toString(), edtPassword.getText().toString());

            }
        });

        //Create New User
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewUser();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Log.w(TAG,"already logged In");
            Toast.makeText(getApplicationContext(), "currentEmail: "+currentUser.getEmail() ,
                    Toast.LENGTH_SHORT).show();
            btnSignOut.setVisibility(View.VISIBLE);
        }
        else{
            btnSignOut.setVisibility(View.GONE);
        }

    }
    private boolean validateForm() {
        boolean valid = true;

        String email = edtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Required.");
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        String password = edtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Required.");
            valid = false;
        } else {
            edtPassword.setError(null);
        }

        return valid;
    }

    public void signIn(String email, String password){
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });



    }

    public void signOut(){

        try {
            mAuth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(mAuth.getCurrentUser() == null) {
            btnSignOut.setVisibility(View.GONE);
        }

    }

    public void NewUser(){
        Intent newIntent = new Intent(this, CreateUserActivity.class);
        this.startActivity(newIntent);
    }


}
