package com.example.studybuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.HashMap;
import java.util.List;

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
        final Activity activity = this;
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

                                Appointment tutor = new Appointment();
                                tutor.setAppId(document.getId());
                                tutor.setLocation(document.get(Location).toString());
                                tutor.setTutor(document.get(TutorId).toString());
                                tutor.setPrice(document.get(Price).toString());
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
                if(tutorProfileMap.containsKey(tutors.get(position).getTutor())){
                    User user = tutorProfileMap.get(tutors.get(position).getTutor());

                    //show tutor name
                    tvTutorName.setText(user.getFirstName() + " "+user.getLastName());

                    //show tutor prefer location and the appointment price
                    tvTutorDescription.setText("Location" + ": " + tutors.get(position).getLocation() + ", " + Price + ": " + tutors.get(position).getPrice());

                    //show user rating
                    rbTutor.setRating(user.getAvgRating());
                    String imgUrl = user.getImg_url();
                    if (imgUrl != null && imgUrl.length() != 0) {
                        try{
                            //resize and noFade is for increasing loading speed
                            Picasso.get().load(imgUrl).resize(150, 100).noFade().into(imgTutor);

                        }catch (Exception e){
                            Picasso.get().load(R.drawable.ic_add_image).into(imgTutor);

                        }

                    }

                }

            //} else {
                //Log.d(TAG, "Tutors and tutors profile amounts not matching!");

            //}

            //set up button to payment activity
            btnSelect = (Button) row.findViewById(R.id.btnSelect);
            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, PaymentActivity.class);
                    i.putExtra(Appointment, tutors.get(position));
                    context.startActivity(i);
                }
            });

            return row;  //once the row is fully constructed, return it.  Hey whatif we had buttons, can we target onClick Events within the rows, yep!
        }

        // TODO: 11/25/2019 : sorting

//        public void sortbytitle() {
//            //make maps of key value paris in the following order: (title,description),(title,image),(titlewebsites)
//            HashMap<String, String> titledescription = new HashMap<String, String>();
//            HashMap<String, Integer> titleimage = new HashMap<String, Integer>();
//            HashMap<String, String> titlewebsites = new HashMap<String, String>();
//            for (int i = 0; i < episodes.length; i++) {
//                titledescription.put(episodes[i], episodeDescriptions[i]);
//                titleimage.put(episodes[i], episodeImages.get(i));
//                titlewebsites.put(episodes[i], links[i]);
//            }
//            //use treeMap structure to sort the keys
//            Map<String, String> sortdescription = new TreeMap<String, String>(titledescription);
//            Map<String, Integer> sortimage = new TreeMap<String, Integer>(titleimage);
//            Map<String, String> sortwebsites = new TreeMap<String, String>(titlewebsites);
//
//            //rearrangin episodes and episode descriptions position
//            Set s = sortdescription.entrySet();
//            Iterator it = s.iterator();
//            int counter = 0;
//            while (it.hasNext()) {
//                Map.Entry entry = (Map.Entry) it.next();
//                String key = (String) entry.getKey();
//                String value = (String) entry.getValue();
//                episodes[counter] = key;
//                episodeDescriptions[counter] = value;
//                counter++;
//            }
//
//            //rearrange episodeImages
//            Set s2 = sortimage.entrySet();
//            Iterator it2 = s2.iterator();
//            int counter2 = 0;
//            while (it2.hasNext()) {
//                Map.Entry entry = (Map.Entry) it2.next();
//                Integer value = (Integer) entry.getValue();
//                episodeImages.set(counter2, value);
//                counter2++;
//            }
//
//            //rearrange links
//            Set s3 = sortwebsites.entrySet();
//            Iterator it3 = s3.iterator();
//            int counter3 = 0;
//            while (it3.hasNext()) {
//                Map.Entry entry = (Map.Entry) it3.next();
//                String value = (String) entry.getValue();
//                links[counter3] = value;
//                counter3++;
//            }
//        }
//
//        public void sortbyrating() {
//            //make maps of key value pair in the following order: (rating,title),(rating,description),(rating,image),(rating,links)
//            HashMap<Float, ArrayList<String>> ratingtitle = new HashMap<Float, ArrayList<String>>();
//            HashMap<Float, ArrayList<String>> ratingdescription = new HashMap<Float, ArrayList<String>>();
//            HashMap<Float, ArrayList<Integer>> ratingimage = new HashMap<Float, ArrayList<Integer>>();
//            HashMap<Float, ArrayList<String>> ratingwebsites = new HashMap<Float, ArrayList<String>>();
//
//            //set up key value pairs for each map
//            for (int i = 0; i < episodes.length; i++) {
//                if (ratingtitle.containsKey(ratings[i])) {
//                    ratingtitle.get(ratings[i]).add(episodes[i]);
//                } else {
//                    ArrayList<String> values = new ArrayList<>();
//                    values.add(episodes[i]);
//                    ratingtitle.put(ratings[i], values);
//                }
//
//                if (ratingdescription.containsKey(ratings[i])) {
//                    ratingdescription.get(ratings[i]).add(episodeDescriptions[i]);
//                } else {
//                    ArrayList<String> values = new ArrayList<>();
//                    values.add(episodeDescriptions[i]);
//                    ratingdescription.put(ratings[i], values);
//                }
//                if (ratingimage.containsKey(ratings[i])) {
//                    ratingimage.get(ratings[i]).add(episodeImages.get(i));
//                } else {
//                    ArrayList<Integer> values = new ArrayList<>();
//                    values.add(episodeImages.get(i));
//                    ratingimage.put(ratings[i], values);
//                }
//                if (ratingwebsites.containsKey(ratings[i])) {
//                    ratingwebsites.get(ratings[i]).add(links[i]);
//                } else {
//                    ArrayList<String> values = new ArrayList<>();
//                    values.add(links[i]);
//                    ratingwebsites.put(ratings[i], values);
//                }
//
//            }
//            //use Treemap structure to sort the maps
//            Map<Float, ArrayList<String>> sorttitle = new TreeMap<Float, ArrayList<String>>(ratingtitle);
//            Map<Float, ArrayList<String>> sortdescription = new TreeMap<Float, ArrayList<String>>(ratingdescription);
//            Map<Float, ArrayList<Integer>> sortimage = new TreeMap<Float, ArrayList<Integer>>(ratingimage);
//            Map<Float, ArrayList<String>> sortwebsites = new TreeMap<Float, ArrayList<String>>(ratingwebsites);
//
//            //rearrange ratings and episodes position
//            Set s = sorttitle.entrySet();
//            Iterator it = s.iterator();
//            int counter = episodes.length - 1;
//            while (it.hasNext()) {
//                Map.Entry entry = (Map.Entry) it.next();
//                Float key = (Float) entry.getKey();
//                ArrayList<String> values = (ArrayList<String>) entry.getValue();
//                for (int i = 0; i < values.size(); i++) {
//                    ratings[counter] = key;
//                    episodes[counter] = values.get(i);
//                    counter--;
//                }
//
//            }
//
//            //rearrange episodeDescriptions position
//            Set s2 = sortdescription.entrySet();
//            Iterator it2 = s2.iterator();
//            int counter2 = episodeDescriptions.length - 1;
//            while (it2.hasNext()) {
//                Map.Entry entry = (Map.Entry) it2.next();
//                ArrayList<String> values = (ArrayList<String>) entry.getValue();
//                for (int i = 0; i < values.size(); i++) {
//                    episodeDescriptions[counter2] = values.get(i);
//                    counter2--;
//                }
//            }
//
//            //rearrange episodeimages position
//            Set s3 = sortimage.entrySet();
//            Iterator it3 = s3.iterator();
//            int counter3 = episodeImages.size() - 1;
//            while (it3.hasNext()) {
//                Map.Entry entry = (Map.Entry) it3.next();
//                ArrayList<Integer> values = (ArrayList<Integer>) entry.getValue();
//                for (int i = 0; i < values.size(); i++) {
//                    episodeImages.set(counter3, values.get(i));
//                    counter3--;
//                }
//            }
//
//            //rearrange links position
//            Set s4 = sortwebsites.entrySet();
//            Iterator it4 = s4.iterator();
//            int counter4 = episodeDescriptions.length - 1;
//            while (it4.hasNext()) {
//                Map.Entry entry = (Map.Entry) it4.next();
//                ArrayList<String> values = (ArrayList<String>) entry.getValue();
//                for (int i = 0; i < values.size(); i++) {
//                    links[counter4] = values.get(i);
//                    counter4--;
//                }
//            }
//        }

    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
//
//        if (parent.getId() == R.id.spinAvailableTutors) {
//            switch (position) {
//                case 0:
//                    tutor = "Pietro";
//                    // Whatever you want to happen when the first item gets selected
//                    //  gender = "Male";
//                    break;
//            }
//        }
//        if (parent.getId() == R.id.spinAvailableTimes) {
//            switch (position) {
//                case 0:
//                    tim = "12:00";
//                    // Whatever you want to happen when the first item gets selected
//                    //  gender = "Male";
//                    break;
//                case 1:
//                    tim = "13:00";
//                    // Whatever you want to happen when the first item gets selected
//                    //  gender = "Male";
//                    break;
//                case 2:
//                    tim = "14:00";
//                    break;
//                case 3:
//                    tim = "15:00";
//                    break;
//                case 4:
//                    tim = "16:00";
//                    break;
//                case 5:
//                    tim = "17:00";
//                    break;
//            }
//        }
//        if (parent.getId() == R.id.spinLocations) {
//            switch (position) {
//                case 0:
//                    loc = "Questrom";
//                    // Whatever you want to happen when the first item gets selected
//                    //  gender = "Male";
//
//                    break;
//                case 1:
//                    loc = "GSU";
//                    // Whate]
//                    // er you want to happen when the first item gets selected
//                    //  gender = "Male";
//                    break;
//                case 2:
//                    loc = "Law";
//                    break;
//            }
//        }
//        if (parent.getId() == R.id.spinDates) {
//            switch (position) {
//                case 0:
//                    date = "4/20/2020";
//                    // Whatever you want to happen when the first item gets selected
//                    //  gender = "Male";
//
//                    break;
//                case 1:
//                    date = "5/20/2020";
//                    // Whatever you want to happen when the first item gets selected
//                    //  gender = "Male";
//                    break;
//            }
//        }
//    }

//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//        // TODO Auto-generated method stub
//        //   gender = null;
//
//    }

    void retrieveSharedPreferenceInfo() {
        //txtFindTut = (TextView) findViewById(R.id.txtTutorClassInfo);
        //txtFindTut.setText(txtFindTut.getText().toString() + " " + tutorCourse);
        SharedPreferences simpleAppInfo = getSharedPreferences("ManageAccountActivity", Context.MODE_PRIVATE);
        tutorCourse = simpleAppInfo.getString("tutor", "<missing>");
    }

    private void addAppointment(Appointment app) {

        DocumentReference ProfileRef = db.collection("Profile").document(mAuth.getCurrentUser().getUid());
        ArrayList<String> appoint = new ArrayList<>();
        appoint.add(app.getCourse());
        appoint.add(app.getTutor());
        appoint.add(app.getLocation());
        appoint.add(app.getDate());

        ProfileRef
                .update("tutor_session", appoint)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Appointment has been added!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Appointment Error!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }



}