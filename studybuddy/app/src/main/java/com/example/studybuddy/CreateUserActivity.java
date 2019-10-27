package com.example.studybuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class CreateUserActivity extends AppCompatActivity {

    Button btnRegister;
    Button btnReturn;
    EditText edtFirstName;
    EditText edtLastName;
    EditText edtClassYear;
    EditText edtEmail;
    EditText edtPassword;
    Spinner genderDropDown;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewuser);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnReturn = (Button) findViewById(R.id.btnBack);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtFirstName = (EditText) findViewById(R.id.edtFirst);
        edtLastName = (EditText) findViewById(R.id.edtLast);
        edtClassYear = (EditText) findViewById(R.id.edtClassYear);

        genderDropDown = (Spinner) findViewById(R.id.spinGender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderDropDown.setAdapter(adapter);


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

            }
        });

    }

    public void goBack(View v) {
        this.finish();
    }


}
