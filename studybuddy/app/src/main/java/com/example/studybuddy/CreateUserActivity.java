package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "EmailPassword";
    private Button btnRegister;
    private Button btnReturn;
    private Button btnAddCourse;

    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtClassYear;
    private EditText edtEmail;
    private EditText edtPassword;
    private Spinner genderDropDown;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final ArrayList<String> paths = new ArrayList<String>(Arrays.asList("Male", "Female"));


    private String gender;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewuser);
        mAuth = FirebaseAuth.getInstance();
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnReturn = (Button) findViewById(R.id.btnBack);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtFirstName = (EditText) findViewById(R.id.edtFirst);
        edtLastName = (EditText) findViewById(R.id.edtLast);
        edtClassYear = (EditText) findViewById(R.id.edtClassYear);
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();


        genderDropDown = (Spinner) findViewById(R.id.spinGender);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateUserActivity.this, android.R.layout.simple_spinner_dropdown_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderDropDown.setAdapter(adapter);
        genderDropDown.setOnItemSelectedListener(this);




        //Return Back to Login Screen
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(v);
            }
        });

        //Register a New User
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String e = edtEmail.getText().toString();
//                String firstN = edtFirstName.getText().toString();
//                String lastN = edtLastName.getText().toString();
//                int year = Integer.valueOf(edtClassYear.getText().toString());
//                String pass = edtPassword.getText().toString();
//                String g = genderDropDown.getSelectedItem().toString();
//                User newUser =  new User(e, firstN, lastN, g , year, pass);
                  createAccount(edtEmail.getText().toString(), edtPassword.getText().toString());

            }
        });

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                gender = "Male";
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                gender = "Female";
                break;


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
        gender = null;

    }



    public void goBack(View v) {
        this.finish();
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

        String first_name = edtFirstName.getText().toString();
        if (TextUtils.isEmpty(first_name)){
            edtFirstName.setError("Required");
            valid = false;

        }
        else{
            edtFirstName.setError(null);
        }

        String last_name = edtFirstName.getText().toString();
        if (TextUtils.isEmpty(last_name)){
            edtLastName.setError("Required");
            valid = false;

        }
        else{
            edtLastName.setError(null);
        }

        if (TextUtils.isEmpty(gender)){
            valid = false;
        }


        return valid;
    }


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uID = generate_UserId();
                                String userId = mAuth.getCurrentUser().getUid();
                                ArrayList<String> courses = new ArrayList<String>();
                                Map<String, Object> profile = new HashMap<>();
                                profile.put("first_name", edtFirstName.getText().toString());
                                profile.put("last_name", edtLastName.getText().toString());
                                profile.put("gender", gender);
                                profile.put("class_year", edtClassYear.getText().toString());
                                profile.put("tutor_class", courses);
                                profile.put("tutor_session",courses);
                                profile.put("your_class", courses);
                                profile.put("image_url","");
                                profile.put("cover_url","");
                                profile.put("user_id", mAuth.getCurrentUser().getUid());

                                    db.collection("Profile").document(userId).set(profile)
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
                            Toast.makeText(getApplicationContext(), "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();

                            HomePage();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }



    public void HomePage(){
        Intent newIntent = new Intent(this, HomePageActivity.class);
        this.startActivity(newIntent);
    }

    public String generate_UserId() {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        return generatedString;
    }


}
