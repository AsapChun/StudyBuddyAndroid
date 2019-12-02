package com.example.studybuddy;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReviewsReceivedActivity extends AppCompatActivity {
    private static final String TAG = "ReviewsReceived";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //Column names of Table Profile on Firebase
    public static final String Profile = "Profile";
    public static final String First_name = "first_name";
    public static final String Last_name = "last_name";
    public static final String Rating = "rating";
    public static final String Review = "review";

    //Listview
    private ListView lvReviewsReceived;
    private ListAdapter lvAdapter;   //Reference to the Adapter used to populate the listview.


    private User currentUser = new User();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewsreceived);

        final Context context = this.getBaseContext();

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Query Profile from cloud firestore and dispaly it
        final DocumentReference docRef = db.collection("Profile").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Map<String, Object> data = document.getData();
                        if (data != null) {

                            //reverse makes the newest first
                            List<String> tmp1 = (List<String>) document.get(Rating);
                            List<String> tmp2 = (List<String>) document.get(Review);
                            Collections.reverse(tmp1);
                            Collections.reverse(tmp2);
                            currentUser.setRatings(tmp1);
                            currentUser.setReviews(tmp2);

                        }

                        lvReviewsReceived = (ListView) findViewById(R.id.lvReviewsReceived);
                        lvAdapter = new MyCustomAdapter(context, currentUser);  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                        lvReviewsReceived.setAdapter(lvAdapter);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    class MyCustomAdapter extends BaseAdapter {

        Context context;   //Creating a reference to our context object, so we only have to get it once.  Context enables access to application specific resources.

        private User currentUser;
        private List<String> ratings;
        private List<String> reviews;

        //STEP 2: Override the Constructor, be sure to:
        public MyCustomAdapter(Context aContext, User currentUser) {
            //initializing our data in the constructor.
            context = aContext;  //saving the context we'll need it again.
            this.currentUser = currentUser;
            this.ratings = currentUser.getRatings();
            this.reviews = currentUser.getReviews();

        }

        //STEP 3: Override and implement getCount(..), ListView uses this to determine how many rows to render.
        @Override
        public int getCount() {
            return ratings == null ? 0 : ratings.size();   //all of the arrays are same length, so return length of any... ick!  But ok for now. :)
        }

        //STEP 4: Override getItem/getItemId, we aren't using these, but we must override anyway.
        @Override
        public Object getItem(int position) {
            return ratings.get(position);        //really should be returning entire set of row data, but it's up to us, and we aren't using this call.
        }

        @Override
        public long getItemId(int position) {
            return position;  //Another call we aren't using, but have to do something since we had to implement (base is abstract).
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {  //convertView is Row (it may be null), parent is the layout that has the row Views.

            //STEP 5a: Inflate the listview row based on the xml.
            View row;  //this will refer to the row to be inflated or displayed if it's already been displayed. (listview_row.xml)


            // Let's optimize a bit by checking to see if we need to inflate, or if it's already been inflated...
            if (convertView == null) {  //indicates this is the first time we are creating this row.
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
                row = inflater.inflate(R.layout.listview_row_reviewsreceived, parent, false);
            } else {
                row = convertView;
            }

            //STEP 5b: Now that we have a valid row instance, we need to get references to the views within that row and fill with the appropriate text and images.
            final TextView tvReviewsReceivedTitle = (TextView) row.findViewById(R.id.tvReviewsReceivedTitle);
            final TextView tvReviewsReceivedDescription = (TextView) row.findViewById(R.id.tvReviewsReceivedDescription);
            final RatingBar rbReviewsReceived = (RatingBar) row.findViewById(R.id.rbReviewsReceived);

            //show user ratings
            tvReviewsReceivedTitle.setText(ratings.get(position));

            //show user reviews
            if (reviews != null && reviews.size() >= position)
                tvReviewsReceivedDescription.setText(reviews.get(position));

            //show user rating
            rbReviewsReceived.setRating(Float.parseFloat(ratings.get(position)));


            return row;  //once the row is fully constructed, return it.  Hey whatif we had buttons, can we target onClick Events within the rows, yep!
        }

    }
}
