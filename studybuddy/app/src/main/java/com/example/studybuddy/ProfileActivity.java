package com.example.studybuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnBack;
    private TextView txtName;
    private TextView txtClassYear;
    private TextView txtClassSubjects;
    private TextView txtTutorSubjects;
    private TextView txtTotalEarnings;
    private RatingBar rbTutorRatings;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        mAuth = FirebaseAuth.getInstance();
        txtName = (TextView) findViewById(R.id.txtName);
        txtClassYear = (TextView) findViewById(R.id.txtClassYear);
        txtClassSubjects = (TextView) findViewById(R.id.txtClassSubjects);
        txtTutorSubjects = (TextView) findViewById(R.id.txtTutorSubjects);
        txtTotalEarnings = (TextView) findViewById(R.id.txtTutorEarnings);
        rbTutorRatings = (RatingBar) findViewById(R.id.rbTutorRatings);
        btnBack = (Button) findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });


    }

    public void goBack() {
        this.finish();
    }



}
