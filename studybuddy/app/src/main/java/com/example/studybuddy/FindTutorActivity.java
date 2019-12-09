package com.example.studybuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.Model.Appointment;
import com.example.studybuddy.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindTutorActivity extends AppCompatActivity {
    private static final String TAG = "Add Appointment";
    private String tutorCourse;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //Column names of Table Appointment on Firebase
    public static final String Appointment = "Appointment";
    public static final String ClassName = "ClassName";
    public static final String Date = "Date";
    public static final String Location = "Location";
    public static final String TutorId = "TutorId";
    public static final String StudentId = "StudentId";
    public static final String Price = "price";
    public static final String ValidAppointment = "validAppointment";

    //Column names of Table Profile on Firebase
    public static final String Profile = "Profile";
    public static final String Image_url = "image_url";
    public static final String First_name = "first_name";
    public static final String Last_name = "last_name";
    public static final String Rating = "rating";

    //Listview
    private ListView lvTutors;
    private ListAdapter lvAdapter;   //Reference to the Adapter used to populate the listview.


    private List<Appointment> tutors = new ArrayList<>();
    private List<User> tutorProfiles = new ArrayList<>();
    private HashMap<String, Appointment> tutorsMap = new HashMap<>();
    private HashMap<String, User> tutorProfileMap = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findtutor);
        retrieveSharedPreferenceInfo();

        final Context context = this.getBaseContext();
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();


        db.collection(Appointment)
                .whereEqualTo(ClassName, tutorCourse)
                //.whereEqualTo(StudentId, null)
                .whereEqualTo(ValidAppointment, false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //cannot be both tutor and student in a appointment
                                if (document.get(TutorId).toString().equals(mAuth.getCurrentUser().getUid()))
                                    continue;

                                Appointment tutor = new Appointment();
                                tutor.setAppId(document.getId());
                                tutor.setLocation(document.get(Location).toString());
                                tutor.setTutor(document.get(TutorId).toString());
                                tutor.setPrice(document.get(Price).toString());
                                tutor.setCourse(document.get(ClassName).toString());
                                tutors.add(tutor);

                                tutorsMap.put(tutor.getTutor(), tutor);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            //get tutor Profile using userid
                            db.collection(Profile)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (!tutorsMap.containsKey(document.getId()))
                                                        continue;

                                                    Appointment tutor = tutorsMap.get(document.getId());

                                                    User tutorProfile = new User();
                                                    tutorProfile.setUserId(tutor.getTutor());
                                                    tutorProfile.setFirstName(document.get(First_name).toString());
                                                    tutorProfile.setLastName(document.get(Last_name).toString());
                                                    tutorProfile.setImg_url(document.get(Image_url).toString());
                                                    tutorProfile.setRatings((List<String>) document.get(Rating));
                                                    tutorProfiles.add(tutorProfile);
                                                    tutorProfileMap.put(tutorProfile.getUserId(), tutorProfile);
                                                }

                                                lvTutors = (ListView) findViewById(R.id.lvTutors);
                                                lvAdapter = new MyCustomAdapter(context, tutors, tutorProfiles);  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                                                lvTutors.setAdapter(lvAdapter);
                                            } else {
                                                Log.d(TAG, "Error getting documents from Profile: ", task.getException());
                                            }
                                        }
                                    });

                        } else {
                            Log.d(TAG, "Error getting documents from Appointment: ", task.getException());
                        }
                    }
                });

    }


    //STEP 1: Create references to needed resources for the ListView Object.  String Arrays, Images, etc.

    class MyCustomAdapter extends BaseAdapter {

        Button btnSelect;   //button to go to the website link of item
        Context context;   //Creating a reference to our context object, so we only have to get it once.  Context enables access to application specific resources.
        List<Appointment> tutors;
        List<User> tutorProfiles;

        //STEP 2: Override the Constructor, be sure to:
        public MyCustomAdapter(Context aContext, List<Appointment> tutors, List<User> tutorProfiles) {
            //initializing our data in the constructor.
            context = aContext;  //saving the context we'll need it again.
            this.tutors = tutors;
            this.tutorProfiles = tutorProfiles;

        }


        //STEP 3: Override and implement getCount(..), ListView uses this to determine how many rows to render.
        @Override
        public int getCount() {
            return tutors == null ? 0 : tutors.size();   //all of the arrays are same length, so return length of any... ick!  But ok for now. :)
        }

        //STEP 4: Override getItem/getItemId, we aren't using these, but we must override anyway.
        @Override
        public Object getItem(int position) {
            return tutors.get(position);        //really should be returning entire set of row data, but it's up to us, and we aren't using this call.
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
                row = inflater.inflate(R.layout.listview_row, parent, false);
            } else {
                row = convertView;
            }

            //STEP 5b: Now that we have a valid row instance, we need to get references to the views within that row and fill with the appropriate text and images.
            ImageView imgTutor = (ImageView) row.findViewById(R.id.imgTutor);  //Q: Notice we prefixed findViewByID with row, why?  A: Row, is the container.
            final TextView tvTutorName = (TextView) row.findViewById(R.id.tvTutorName);
            final TextView tvTutorDescription = (TextView) row.findViewById(R.id.tvTutorDescription);
            final RatingBar rbTutor = (RatingBar) row.findViewById(R.id.rbTutor);

            //if (position < tutors.size() && position < tutorProfiles.size()) {
            if (tutorProfileMap.containsKey(tutors.get(position).getTutor())) {
                User user = tutorProfileMap.get(tutors.get(position).getTutor());

                //show tutor name
                tvTutorName.setText(user.getFirstName() + " " + user.getLastName());

                //show tutor prefer location and the appointment price
                tvTutorDescription.setText("Location" + ": " + tutors.get(position).getLocation() + ", " + Price + ": " + tutors.get(position).getPrice());

                //show user rating
                rbTutor.setRating(user.getAvgRating());
                String imgUrl = user.getImg_url();
                if (imgUrl != null && imgUrl.length() != 0) {
                    try {
                        //resize and noFade is for increasing loading speed
                        Picasso.get().load(imgUrl).resize(150, 100).noFade().into(imgTutor);

                    } catch (Exception e) {
                        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_person_white_24dp);
                        imgTutor.setImageDrawable(myDrawable);

                    }

                } else {

                    Drawable myDrawable = getResources().getDrawable(R.drawable.ic_person_white_24dp);
                    imgTutor.setImageDrawable(myDrawable);
                }

            }

            //set up button to payment activity
            btnSelect = (Button) row.findViewById(R.id.btnSelect);
            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, PaymentActivity.class);
                    i.putExtra(Appointment, tutors.get(position));
                    i.putExtra(Profile, tutorProfileMap.get(tutors.get(position).getTutor()));
                    startActivity(i);
                }
            });

            return row;  //once the row is fully constructed, return it.  Hey whatif we had buttons, can we target onClick Events within the rows, yep!
        }

        //sorting the tutors, lower prices first
        public void sortbyPrice() {
            Collections.sort(tutors, new Comparator<Appointment>() {
                @Override
                public int compare(Appointment u1, Appointment u2) {
                    return u1.getPrice().compareTo(u2.getPrice());
                }
            });
        }

        //sorting the tutors, higher ratings first
        public void sortbyRating() {
            Collections.sort(tutorProfiles, new Comparator<User>() {
                @Override
                public int compare(User u1, User u2) {
                    return Double.compare(u2.getAvgRating(), u1.getAvgRating());
                }
            });

            //modify the order of tutors to the order of tutorProfiles; because we generate the view according to the order of tutors
            List<Appointment> tutorsTmp = new ArrayList<>();
            Boolean breakFlag = false;
            for (User u : tutorProfiles) {
                breakFlag = false;
                for (Map.Entry<String, User> userEntry : tutorProfileMap.entrySet()) {
                    if (u.hashCode() == userEntry.getValue().hashCode())
                        for (Map.Entry<String, Appointment> appEntry : tutorsMap.entrySet())
                            if (userEntry.getKey() == appEntry.getKey()) {
                                tutorsTmp.add(appEntry.getValue());
                                breakFlag = true;
                                break;
                            }
                    if (breakFlag)
                        break;
                }

            }
            tutors = tutorsTmp;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.findtutor_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        lvTutors = (ListView) findViewById(R.id.lvTutors);

        MyCustomAdapter a = (MyCustomAdapter) lvAdapter;
        lvTutors.setAdapter(a);

        //sort list item by title
        if (id == R.id.mSortByPrice) {
            a.sortbyPrice();
            return true;
        }

        //sort list item by rating
        if (id == R.id.mSortByRating) {
            a.sortbyRating();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void retrieveSharedPreferenceInfo() {
        //txtFindTut = (TextView) findViewById(R.id.txtTutorClassInfo);
        //txtFindTut.setText(txtFindTut.getText().toString() + " " + tutorCourse);
        SharedPreferences simpleAppInfo = getSharedPreferences("ManageAccountActivity", Context.MODE_PRIVATE);
        tutorCourse = simpleAppInfo.getString("tutor", "<missing>");
    }

}
