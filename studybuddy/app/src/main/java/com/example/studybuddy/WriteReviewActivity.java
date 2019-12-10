package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.studybuddy.Model.Appointment;
import com.example.studybuddy.Model.Profile;
import com.example.studybuddy.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteReviewActivity extends AppCompatActivity {

    //static string for table or column names on firebase
    public static final String Appointment = "Appointment";
    public static final String Profile = "Profile";
    public static final String rating = "rating";
    public static final String review = "review";
    public static final String rated = "rated";
    public static final String validAppointment = "validAppointment";

    //widgets on the page
    private RatingBar rbTutor;
    private EditText edtComment;
    private Button btnSend;
    private Appointment app;
    private User tutor;

    //firebase init
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        Intent intent = getIntent();
        app = (Appointment) intent.getSerializableExtra(Appointment);
        tutor = (User) intent.getSerializableExtra(Profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rbTutor = (RatingBar) findViewById(R.id.rbTutor);
        edtComment = (EditText) findViewById(R.id.edtComment);
        btnSend = (Button) findViewById(R.id.btnSend);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {

                    //using batch to update two document at the same time
                    WriteBatch batch = db.batch();

                    //Update rated column on Appointment Table False->True
                    DocumentReference appRef = db.collection(Appointment).document(app.getAppId());
                    batch.update(appRef, rated, Boolean.TRUE);
                    batch.update(appRef, validAppointment, Boolean.FALSE);

                    //Add the rating and comments to Tutor's profile
                    DocumentReference proRef = db.collection(Profile).document(tutor.getUserId());

                    String strNewRating = String.valueOf(rbTutor.getRating());
                    List<String> tutorRatings = tutor.getRatings();
                    tutorRatings.add(strNewRating);

                    batch.update(proRef, rating, tutorRatings);

                    String strNewComment = edtComment.getText().toString();
                    List<String> tutorReviews = tutor.getReviews();
                    tutorReviews.add(strNewComment);

                    batch.update(proRef, review, tutorReviews);

                    // Commit the batch
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Review Sent...", Toast.LENGTH_SHORT).show();
                            goToHome();
                        }
                    });

                }

            }
        });
    }

    public void goToHome() {

        Intent i = new Intent(getBaseContext(), HomePageActivity.class);
        startActivity(i);
    }


}
