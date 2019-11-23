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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FindTutorActivity extends AppCompatActivity {
    private static final String TAG = "Add Appointment";
    /*private Button btnConfirm;
    private Spinner AvailableTutorDropDown;
    private Spinner AvailableTimesDropDown;
    private Spinner AvailableDatesDropDown;
    private Spinner LocationsDropDown;
    private static final ArrayList<String> tutorpaths = new ArrayList<String>(Arrays.asList("Pietro")); //get available tutors from firebase
    private static final ArrayList<String> timespaths = new ArrayList<String>(Arrays.asList( "12:00", "13:00", "14:00", "15:00", "16:00", "17:00")); //get available tutors from firebase
    private static final ArrayList<String> datespaths = new ArrayList<>(Arrays.asList( "4/20/2020", "5/20/2020"));
    private static final ArrayList<String> locationspaths = new ArrayList<>(Arrays.asList("Questrom", "GSU", "Law"));
    private String tutor;
    private String date;
    private String tim;
    private String loc;*/
    private String tutorCourse;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //Listview
    private ListView lvTutors;
    private ListAdapter lvAdapter;   //Reference to the Adapter used to populate the listview.


    private List<User> tutors = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findtutor);
        retrieveSharedPreferenceInfo();

        final Context context = this.getBaseContext();
        final Activity activity = this;
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        db.collection("Appointment")
                .whereEqualTo("ClassName", tutorCourse)
                .whereEqualTo("StudentID", null)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            String tutorDescriptions[] = {};  //the "better" way is to encapsulate the list items into an object, then create an arraylist of objects.
                            //     int episodeImages[];         //this approach is fine for now.
                            ArrayList<Integer> tutorImages;  //Well, we can use one arrayList too...  Just mixing it up, Arrays or Templated ArrayLists, you choose.
                            Float ratings[];       //keep track of all ratings

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User tutor = new User();
                                //tutor.email =  = document.get("TutorID").toString();;
                                tutors.add(tutor);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            lvTutors = (ListView) findViewById(R.id.lvTutors);
                            lvAdapter = new MyCustomAdapter(context, tutors);  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                            lvTutors.setAdapter(lvAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        /*if(tutors == null){
            Toast.makeText(getApplicationContext(), "tutors is null", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(getApplicationContext(), tutors[tutorsLength], Toast.LENGTH_SHORT).show();
        }*/

        /*lvTutors = (ListView) findViewById(R.id.lvTutors);
        lvAdapter = new MyCustomAdapter(this.getBaseContext(), this);  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
        lvTutors.setAdapter(lvAdapter);*/

        //btnConfirm = (Button) findViewById(R.id.btnCreate);

        /*AvailableTutorDropDown = (Spinner) findViewById(R.id.spinAvailableTutors);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, tutorpaths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AvailableTutorDropDown.setAdapter(adapter);
        AvailableTutorDropDown.setOnItemSelectedListener(this);

        AvailableDatesDropDown = (Spinner) findViewById(R.id.spinDates);
        ArrayAdapter<String> dates = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, datespaths);
        dates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AvailableDatesDropDown.setAdapter(dates);
        AvailableDatesDropDown.setOnItemSelectedListener(this);

        AvailableTimesDropDown = (Spinner) findViewById(R.id.spinAvailableTimes);
        ArrayAdapter<String> times = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, timespaths);
        times.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AvailableTimesDropDown.setAdapter(times);
        AvailableTimesDropDown.setOnItemSelectedListener(this);

        LocationsDropDown = (Spinner) findViewById(R.id.spinLocations);
        ArrayAdapter<String> locations = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_spinner_dropdown_item, locationspaths);
        locations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LocationsDropDown.setAdapter(locations);
        LocationsDropDown.setOnItemSelectedListener(this);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appointment appoint = new Appointment(tutorCourse, tutor, loc, date, tim );
                addAppointment(appoint);
                goToHomePage();
            }
        });*/
    }


//STEP 1: Create references to needed resources for the ListView Object.  String Arrays, Images, etc.

    class MyCustomAdapter extends BaseAdapter {

        //private String websites[];     //keep track of all websites
        //private String links[];        //keep track of the position of website links corresponding to the correct item
        //    ArrayList<String> episodes;
        //    ArrayList<String> episodeDescriptions;

        Button btnSelect;   //button to go to the website link of item
        Context context;   //Creating a reference to our context object, so we only have to get it once.  Context enables access to application specific resources.
        Activity activity;
        // Eg, spawning & receiving intents, locating the various managers.

        //STEP 2: Override the Constructor, be sure to:
        // grab the context, we will need it later, the callback gets it as a parm.
        // load the strings and images into object references.
        public MyCustomAdapter(Context aContext, List<User> tutors) {
            //initializing our data in the constructor.
            context = aContext;  //saving the context we'll need it again.

            /*episodes = aContext.getResources().getStringArray(R.array.episodes);  //retrieving list of episodes predefined in strings-array "episodes" in strings.xml
            episodeDescriptions = aContext.getResources().getStringArray(R.array.episode_descriptions);
            websites = aContext.getResources().getStringArray(R.array.websites);*/

            //initialize ratins, links array
            /*ratings = new Float[episodes.length];
            links = new String[episodes.length];
            for (int i = 0; i < ratings.length; i++) {
                ratings[i] = (float) 0;
                links[i] = websites[i];
            }*/


//This is how you would do it if you were using an ArrayList, leaving code here for reference, though we could use it instead of the above.
//        episodes = (ArrayList<String>) Arrays.asList(aContext.getResources().getStringArray(R.array.episodes));  //retrieving list of episodes predefined in strings-array "episodes" in strings.xml
//        episodeDescriptions = (ArrayList<String>) Arrays.asList(aContext.getResources().getStringArray(R.array.episode_descriptions));  //Also casting to a friendly ArrayList.


            /*episodeImages = new ArrayList<Integer>();   //Could also use helper function "getDrawables(..)" below to auto-extract drawable resources, but keeping things as simple as possible.
            episodeImages.add(R.drawable.st_spocks_brain);
            episodeImages.add(R.drawable.st_arena__kirk_gorn);
            episodeImages.add(R.drawable.st_this_side_of_paradise__spock_in_love);
            episodeImages.add(R.drawable.st_mirror_mirror__evil_spock_and_good_kirk);
            episodeImages.add(R.drawable.st_platos_stepchildren__kirk_spock);
            episodeImages.add(R.drawable.st_the_naked_time__sulu_sword);
            episodeImages.add(R.drawable.st_the_trouble_with_tribbles__kirk_tribbles);*/
        }


        //STEP 3: Override and implement getCount(..), ListView uses this to determine how many rows to render.
        @Override
        public int getCount() {
            return tutors == null ? 0 : tutors.size();   //all of the arrays are same length, so return length of any... ick!  But ok for now. :)
        }

        //STEP 4: Override getItem/getItemId, we aren't using these, but we must override anyway.
        @Override
        public Object getItem(int position) {
//        return episodes.get(position);  //In Case you want to use an ArrayList
            return tutors.get(position);        //really should be returning entire set of row data, but it's up to us, and we aren't using this call.
        }

        @Override
        public long getItemId(int position) {
            return position;  //Another call we aren't using, but have to do something since we had to implement (base is abstract).
        }

        //THIS IS WHERE THE ACTION HAPPENS.  getView(..) is how each row gets rendered.
//STEP 5: Easy as A-B-C
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {  //convertView is Row (it may be null), parent is the layout that has the row Views.

//STEP 5a: Inflate the listview row based on the xml.
            View row;  //this will refer to the row to be inflated or displayed if it's already been displayed. (listview_row.xml)
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        row = inflater.inflate(R.layout.listview_row, parent, false);  //

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


            tvTutorName.setText(tutors.get(position).getName());
            tvTutorDescription.setText("comments");
            //TODO: update tutorImages
            //imgTutor.setImageResource(tutorImages.get(position).intValue());

            //find rating bar, and store the rating from current rating bar
            final RatingBar rbTutor = (RatingBar) row.findViewById(R.id.rbTutor);
            //TODO: update rating
            //ratings[position] = rbTutor.getRating();

            //set up ratingbar change listener
            /*rbEpisode.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                public void onRatingChanged(RatingBar ratingBar, float rating,
                                            boolean fromUser) {

                    SharedPreferences simpleAppInfo = context.getSharedPreferences("ratingInfo", MODE_PRIVATE);
                    ;
                    SharedPreferences.Editor editor = simpleAppInfo.edit();

                    //store rating, title, desctiption of current row for recovery
                    editor.putString("rating" + tvEpisodeTitle.getText(), Float.toString(rating));
                    editor.putString("title" + tvEpisodeTitle.getText(), (String) tvEpisodeTitle.getText());
                    editor.putString("description" + tvEpisodeDescription.getText(), (String) tvEpisodeDescription.getText());

                    //remember current for rearrangement later
                    ratings[position] = rbEpisode.getRating();
                    editor.apply();
                    //Toast.makeText(this, "Shared Preference Data Updated.", Toast.LENGTH_LONG).show();
                }
            });*/

            //recover rating, title, description if value is not missing
            /*SharedPreferences simpleAppInfo = context.getSharedPreferences("ratingInfo", MODE_PRIVATE);
            ;
            String rating = simpleAppInfo.getString("rating" + tvEpisodeTitle.getText(), "<missing>");
            String title = simpleAppInfo.getString("title" + tvEpisodeTitle.getText(), "<missing>");
            String description = simpleAppInfo.getString("description" + tvEpisodeDescription.getText(), "<missing>");

            if (rating != "<missing>") {
                rbEpisode.setRating(Float.parseFloat(rating));
                tvEpisodeTitle.setText(title);
                tvEpisodeDescription.setText(description);

            }
            */

            //set up button to link item to website
            btnSelect = (Button) row.findViewById(R.id.btnSelect);
            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(links[position])));

                }
            });

//STEP 5c: That's it, the row has been inflated and filled with data, return it.
            return row;  //once the row is fully constructed, return it.  Hey whatif we had buttons, can we target onClick Events within the rows, yep!
//return convertView;

        }

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
        appoint.add(app.getTime());

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

    public void goToHomePage() {
        Intent newIntent = new Intent(this, HomePageActivity.class);
        this.startActivity(newIntent);
    }

}
