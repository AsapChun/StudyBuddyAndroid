package com.example.studybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btnNewUser;
    private Button btnLogin;
    private Button btnDonations;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNewUser = (Button) findViewById(R.id.btnNewUser);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnDonations = (Button) findViewById(R.id.btnTestPay);
        btnWebsite = (Button) findViewById(R.id.btnOpen);

        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        //Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Create New User
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewUser(v);
            }
        });

        //Stripe test test
        btnDonations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayUser(v);
            }
        });

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webpage = Uri.parse("https://stripe.com/");
                Intent newIntent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(newIntent);
            }
        });
    }

    public void NewUser(View v){
        Intent newIntent = new Intent(this, CreateUserActivity.class);
        this.startActivity(newIntent);
    }

    public void PayUser(View v){
        Intent newIntent = new Intent(this, PaymentActivity.class);
        this.startActivity(newIntent);
    }

}
