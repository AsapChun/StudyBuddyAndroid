package com.example.studybuddy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.example.studybuddy.Model.Appointment;
import com.example.studybuddy.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import org.w3c.dom.Text;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity {
    private static final String TAG = "HomePage";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase;
    private ArrayList<String> classes;
    private ArrayList<String> tutorAppointments;
    private ArrayList<String> studentAppointments;
    private ArrayList<String> Names;
    private ArrayList<String> studentNames;
    private ArrayList<String> tutorNames;
    private Map<String, Object> profile;

    //private TextView txtSessions;

    //listview
    private ListView lvAppointment;
    private ListAdapter lvAdapter;


    private boolean update=true;
    private boolean studentupdate=true;
    private boolean tutorupdate=false;
    private ArrayList<Boolean> checkname;
    private ArrayList<Boolean> checknameagain;
    private ProgressDialog progress;
    private boolean checkclass=true;    //boolean check if class need to be updated
    private boolean renderS=true;
    private boolean renderT=true;
    private String appointment;

    private ArrayList<Appointment> tutors = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //txtSessions = (TextView) findViewById(R.id.txtTutorSessions);
        //txtSessions.setMovementMethod(new ScrollingMovementMethod());
        lvAppointment = findViewById(R.id.lvAppointment);

        //add loading screen
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please Wait while Loading Data...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog


        classes = new ArrayList<>();
        tutorAppointments = new ArrayList<>();
        studentAppointments = new ArrayList<>();
        Names = new ArrayList<>();
        studentNames = new ArrayList<>();
        tutorNames = new ArrayList<>();
        checkname = new ArrayList<>();
        checknameagain = new ArrayList<>();


        //retrieve data from bundle, and store them in variables to be used later
        //and retrieve text to display courses and appointments in this activity.
        Bundle b = getIntent().getExtras();
        if(b!=null){
            for(String key: b.keySet()){
                if(key.equals("update")){
                    update = b.getBoolean("update");

                }
                else if(key.equals("studentupdate")){
                    studentupdate = b.getBoolean("studentupdate");
                }
                else if(key.equals("tutorupdate")){
                    tutorupdate = b.getBoolean("tutorupdate");
                }

                else if(key.equals("studentAppointments")){
                    studentAppointments = b.getStringArrayList("studentAppointments");

                }
                else if (key.equals("tutorAppointments")){
                    tutorAppointments = b.getStringArrayList("tutorAppointments");
                }
                else if(key.equals("studentNames")){
                    studentNames = b.getStringArrayList("studentNames");
                }
                else if(key.equals("tutorNames")){
                    tutorNames = b.getStringArrayList("tutorNames");
                }
                else if(key.equals("checkname")){
                    checkname = (ArrayList<Boolean>)b.getSerializable("checkname");
                }
                else if(key.equals("checknameagain")){
                    checknameagain = (ArrayList<Boolean>)b.getSerializable("checknameagain");
                }
                else if(key.equals("checkclass")){
                    checkclass = b.getBoolean("checkclass");
                }
                else if(key.equals("renderS")){
                    renderS = b.getBoolean("renderS");
                }
                else if(key.equals("renderT")){
                    renderT = b.getBoolean("renderT");
                }
                else if(key.equals("tutor")){
                    tutors = (ArrayList<Appointment>) b.getSerializable("tutor");
                }
            }
        }

        //if no appointment updated are needed, finish loading
        if(!studentupdate && !tutorupdate){
            update=false;
            progress.dismiss();

            ArrayList<String> appointmentDisplay = new ArrayList<>();
            for(String apt: studentAppointments){
                appointmentDisplay.add(apt+"<br>");
            }
            for(String apt: tutorAppointments){
                appointmentDisplay.add(apt+"<br>");
            }
            lvAdapter = new MyCustomAdapter(this, appointmentDisplay , tutors);
            lvAppointment.setAdapter(lvAdapter);
        }

        //check if any update to the homepage is needed
        if(update) {
            progress.show();
            getHomePageData();
        }

    }

    //retrieve data for classes and appointments
    private void getHomePageData(){


        //check if appointment for user as student needs to be updated
        if(studentupdate){
            //getAppointmentAsStudent();
            getstudentappointment();
        }
        //check if appointment for user as tutor needs to be updated
        if(tutorupdate){
            //getAppointmentAsTutor();
            gettutorappointment();
        }


    }

    //retrieve data for appointments for users as a student
    private void getstudentappointment(){
        db.collection("Appointment")
                .whereEqualTo("StudentId",mAuth.getCurrentUser().getUid())
                .whereEqualTo("rated",false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        int counterS=0;
                        //boolean to check if query is empty
                        boolean empty=true;
                        for (QueryDocumentSnapshot doc : value) {
                            final String class_d = (String) doc.getData().get("ClassName");
                            final String location_d = (String) doc.getData().get("Location");
                            final ArrayList<String> day_d = (ArrayList<String>) doc.getData().get("Date");
                            String tutor_d = (String) doc.getData().get("TutorId");
                            Boolean valid = (Boolean) doc.getData().get("validAppointment");
                            Log.d(TAG, doc.getId() + " appointment at " + location_d);
                            if(class_d!=null &&valid ){
                                empty=false;
                                checknameagain.add(true);
                                //retrieve tutor name from database with a nested query
                                DocumentReference docRef = db.collection("Profile").document(tutor_d);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    int pos = checknameagain.size()-1;
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document != null) {
                                                Map<String, Object> data = document.getData();
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                String fname = (String) data.get("first_name");
                                                String lname = (String) data.get("last_name");
                                                String name= fname+" "+lname;
                                                //studentNames.add(fname+" "+lname);

                                                //formatt appointment text
                                                String days ="";
                                                int counter=0;
                                                if(!day_d.isEmpty()){
                                                    for(String day: day_d){
                                                        if(counter==day_d.size()-1){
                                                            days+=day;
                                                        }else{
                                                            days+=day+", ";
                                                        }
                                                        counter+=1;
                                                    }

                                                }




                                                //store the text to display each appointment into array
                                                String s = "You have a " + "<i>"+class_d+"</i>" + " appointment with " + "<b>"+name
                                                        +"</b>"+
                                                        " at " + location_d + " on " + days+"\n";

                                                Appointment tutor = new Appointment();
                                                tutor.setAppId(doc.getId());
                                                tutor.setLocation(location_d);
                                                tutor.setCourse(class_d);
                                                tutor.setTutor(name);
                                                tutor.setDate(days);
                                                tutors.add(tutor);

                                                studentAppointments.add(s);
                                                checknameagain.set(pos,false);


                                            }
                                            if(!checknameagain.get(checknameagain.size()-1)){
                                                Intent i = new Intent(getBaseContext(), HomePageActivity.class);
                                                Bundle b = new Bundle();
                                                b.putBoolean("update",true);
                                                b.putBoolean("studentupdate",false);
                                                b.putBoolean("checkclass",false);
                                                b.putBoolean("tutorupdate",true);

                                                b.putBoolean("renderS",renderS);
                                                b.putBoolean("renderT",renderT);

                                                b.putStringArrayList("studentNames",studentNames);
                                                b.putStringArrayList("tutorNames",tutorNames);
                                                b.putStringArrayList("classes",classes);
                                                b.putStringArrayList("studentAppointments",studentAppointments);
                                                b.putStringArrayList("tutorAppointments",tutorAppointments);

                                                b.putSerializable("tutor", tutors);
                                                i.putExtras(b);
                                                goBack();
                                                startActivity(i);
                                            }
                                        }
                                    }

                                });




                            }
                            counterS+=1;
                        }
                        //save the data needed, and restart current activity
                        if(empty){
                            Intent i = new Intent(getBaseContext(), HomePageActivity.class);
                            Bundle b = new Bundle();
                            b.putBoolean("update",true);
                            b.putBoolean("studentupdate",false);
                            b.putBoolean("checkclass",false);
                            b.putBoolean("tutorupdate",true);

                            b.putBoolean("renderS",renderS);
                            b.putBoolean("renderT",renderT);

                            b.putStringArrayList("studentNames",studentNames);
                            b.putStringArrayList("tutorNames",tutorNames);
                            b.putStringArrayList("classes",classes);
                            b.putStringArrayList("studentAppointments",studentAppointments);
                            b.putStringArrayList("tutorAppointments",tutorAppointments);

                            b.putSerializable("tutor", tutors);
                            i.putExtras(b);
                            goBack();
                            startActivity(i);
                        }
                    }
                });
    }

    //retrieve data for appointments for user as a tutor
    private void gettutorappointment(){
        db.collection("Appointment")
                .whereEqualTo("TutorId",mAuth.getCurrentUser().getUid())
                .whereEqualTo("validAppointment",true)
                .whereEqualTo("rated",false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        int counterS=0;
                        //boolean to check if query is empty
                        boolean empty=true;
                        for (QueryDocumentSnapshot doc : value) {
                            final String class_d = (String) doc.getData().get("ClassName");
                            final String location_d = (String) doc.getData().get("Location");
                            final ArrayList<String> day_d = (ArrayList<String>) doc.getData().get("Date");
                            String student_d = (String) doc.getData().get("StudentId");
                            Boolean valid = (Boolean) doc.getData().get("validAppointment");
                            Log.d(TAG, doc.getId() + " appointment at " + location_d);
                            if(class_d!=null &&valid ){
                                empty=false;
                                checkname.add(true);

                                //retrieve student name from database with a nested query
                                DocumentReference docRef = db.collection("Profile").document(student_d);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    int pos = checkname.size()-1;
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document != null) {
                                                Map<String, Object> data = document.getData();
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                String fname = (String) data.get("first_name");
                                                String lname = (String) data.get("last_name");
                                                String name= fname+" "+lname;
                                                //studentNames.add(fname+" "+lname);

                                                //formatt appointment text
                                                String days ="";
                                                int counter=0;
                                                for(String day: day_d){
                                                    if(counter==day_d.size()-1){
                                                        days+=day;
                                                    }else{
                                                        days+=day+", ";
                                                    }
                                                    counter+=1;
                                                }

                                                //store the text to display each appointment into array
                                                String s = "You have a " + "<i>"+class_d+"</i>" + " tutoring session with " + "<b>"+name
                                                        +"</b>"+
                                                        " at " + location_d + " on " + days+"\n";

                                                Appointment tutor = new Appointment();
                                                tutor.setAppId(doc.getId());
                                                tutor.setLocation(location_d);
                                                tutor.setDate(days);
                                                tutor.setStudent(name);
                                                tutor.setCourse(class_d);
                                                tutors.add(tutor);

                                                tutorAppointments.add(s);
                                                checkname.set(pos,false);
                                                


                                            }
                                            if(!checkname.get(checkname.size()-1)){
                                                Intent i = new Intent(getBaseContext(), HomePageActivity.class);
                                                Bundle b = new Bundle();
                                                b.putBoolean("update",true);
                                                b.putBoolean("studentupdate",studentupdate);
                                                b.putBoolean("checkclass",false);
                                                b.putBoolean("tutorupdate",false);

                                                b.putBoolean("renderS",renderS);
                                                b.putBoolean("renderT",renderT);

                                                b.putStringArrayList("studentNames",studentNames);
                                                b.putStringArrayList("tutorNames",tutorNames);
                                                b.putStringArrayList("classes",classes);
                                                b.putStringArrayList("studentAppointments",studentAppointments);
                                                b.putStringArrayList("tutorAppointments",tutorAppointments);

                                                b.putSerializable("tutor", tutors);
                                                i.putExtras(b);
                                                goBack();
                                                startActivity(i);
                                            }
                                        }
                                    }

                                });




                            }
                            counterS+=1;
                        }
                        //if no result found, no more update is needed and restart current activity
                        if(empty){
                            Intent i = new Intent(getBaseContext(), HomePageActivity.class);
                            Bundle b = new Bundle();
                            b.putBoolean("update",true);
                            b.putBoolean("studentupdate",studentupdate);
                            b.putBoolean("checkclass",false);
                            b.putBoolean("tutorupdate",false);

                            b.putBoolean("renderS",renderS);
                            b.putBoolean("renderT",renderT);

                            b.putStringArrayList("studentNames",studentNames);
                            b.putStringArrayList("tutorNames",tutorNames);
                            b.putStringArrayList("classes",classes);
                            b.putStringArrayList("studentAppointments",studentAppointments);
                            b.putStringArrayList("tutorAppointments",tutorAppointments);

                            b.putSerializable("tutor", tutors);
                            i.putExtras(b);
                            goBack();
                            startActivity(i);
                        }
                    }
                });
    }


    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.idManageAccount:
                Toast.makeText(getApplicationContext(), "Manage Account Selected", Toast.LENGTH_SHORT).show();
                goToManageAccount();
                return true;
            case R.id.itmProfile:
                Toast.makeText(getApplicationContext(), "Profile Selected", Toast.LENGTH_SHORT).show();
                goToProfile();
                return true;
            case R.id.itmMessages:
                Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_SHORT).show();
                goToMessages();
                return true;
            case R.id.itmReviews:
                goToReviews();
                return true;
            case R.id.itmLocation:
                goToLocation();
                return true;
            case R.id.itmSettings:
                Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_SHORT).show();
                goToSettings();
                return true;
            case R.id.itmLogoff:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goBack() {
        Intent newIntent = new Intent(this, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(newIntent);
    }

    public void goToSettings(){
        Intent newIntent = new Intent(this, SettingsActivity.class);
        this.startActivity(newIntent);
    }
    public void goToPayment(){
        Intent newIntent = new Intent(this, PaymentActivity.class);
        this.startActivity(newIntent);
    }
    public void goToReviews(){
        Intent newIntent = new Intent(this, ReviewActivity.class);
        this.startActivity(newIntent);
    }
    public void goToProfile(){
        Intent newIntent = new Intent(this, ProfileActivity.class);
        this.startActivity(newIntent);
    }
    public void goToLocation(){
        Intent newIntent = new Intent(this, LocationActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("update",true);
        newIntent.putExtras(b);
        this.startActivity(newIntent);
    }
    public void goToManageAccount(){
        Intent newIntent = new Intent(this, ManageAccountActivity.class);
        this.startActivity(newIntent);
    }
    public void goToMessages(){
        Intent newIntent = new Intent(this, MessageFragmentActivity.class);
        this.startActivity(newIntent);
    }
    public void goToManage(){
        Intent newIntent = new Intent(this, ManageReservationActivity.class);
        this.startActivity(newIntent);
    }


    public void signOut(){

        try {
            mAuth.signOut();
            goBack();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //press back button on homepage, go back to home screen of the device
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //destroy loading screen if current activity is destroy
        if ( progress!=null && progress.isShowing() ){
            progress.cancel();
        }
    }

    //STEP 1: Create references to needed resources for the ListView Object.  String Arrays, Images, etc.

    class MyCustomAdapter extends BaseAdapter {

        Context context;   //Creating a reference to our context object, so we only have to get it once.  Context enables access to application specific resources.
        ArrayList<String> appointments;
        ArrayList<Appointment> tutors;

        //STEP 2: Override the Constructor, be sure to:
        public MyCustomAdapter(Context aContext, ArrayList<String> apt, ArrayList<Appointment> tutors) {
            //initializing our data in the constructor.
            context = aContext;  //saving the context we'll need it again.
            appointments =apt;
            this.tutors = tutors;


        }


        //STEP 3: Override and implement getCount(..), ListView uses this to determine how many rows to render.
        @Override
        public int getCount() {
            return appointments == null ? 0 : appointments.size();   //all of the arrays are same length, so return length of any... ick!  But ok for now. :)
        }

        //STEP 4: Override getItem/getItemId, we aren't using these, but we must override anyway.
        @Override
        public Object getItem(int position) {
            return appointments.get(position);        //really should be returning entire set of row data, but it's up to us, and we aren't using this call.
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
                row = inflater.inflate(R.layout.item_appointments, parent, false);
            } else {
                row = convertView;
            }

            //STEP 5b: Now that we have a valid row instance, we need to get references to the views within that row and fill with the appropriate text and images.
            //Q: Notice we prefixed findViewByID with row, why?  A: Row, is the container.


            //txtDesc.setText(Html.fromHtml(appointments.get(position)));
            Appointment apt = tutors.get(position);
            //bug needs fix--> java.lang.IndexOutOfBoundsException: Invalid index 0, size is 0
            final TextView txtDesc = (TextView) row.findViewById(   R.id.txtDesc);
            final CardView cardView = (CardView) row.findViewById(R.id.cardView);
            final TextView txtTitle = (TextView) row.findViewById(R.id.txtTitle);
            final ImageView image = row.findViewById(R.id.image);
            txtTitle.setText(Html.fromHtml("<b>"+apt.getCourse()+"</b>"));

            if(apt.getStudent()!=null && apt.getTutor() == null){
                String s = "You are" + " tutoring " + "<b>" + apt.getStudent()+ "</b>" +
                        " at " + "<b>" + apt.getLocation() + "</b>" + " on " +  "<b>" + apt.getDate() +"</b>" +"\n";
                Picasso.get().load(R.drawable.tutoring_image).into(image);
                txtDesc.setText(Html.fromHtml(s));
            }else if(apt.getTutor()!=null && apt.getStudent()==null){
//                String s = "You have an" + " appointment with tutor " + apt.getTutor()
//                        + " at " + apt.getLocation() + " on " + apt.getDate()+"\n";
                String s = "You have an" + " appointment with tutor " + "<b>" + apt.getTutor()+ "</b>" +
                        " at " + "<b>" + apt.getLocation() + "</b>" + " on " +  "<b>" + apt.getDate() +"</b>" +"\n";
                Picasso.get().load(R.drawable.default_cardview).into(image);
                txtDesc.setText(Html.fromHtml(s));


            }
            else{
                Log.w(TAG, "Error!");
            }

            cardView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent i = new Intent(context, ManageReservationActivity.class);
                    i.putExtra("Appointment", tutors.get(position));
                    startActivity(i);
                }
            });



            return row;  //once the row is fully constructed, return it.  Hey whatif we had buttons, can we target onClick Events within the rows, yep!
        }

    }

    void saveSharedPreferenceInfo(){
        //1. Refer to the SharedPreference Object.
        SharedPreferences AppInfo = getSharedPreferences("HomePageActivity", Context.MODE_PRIVATE);  //Private means no other Apps can access this.

        //2. Create an Shared Preferences Editor for Editing Shared Preferences.
        //Note, not a real editor, just an object that allows editing...

        SharedPreferences.Editor editor = AppInfo.edit();

        //3. Store what's important!  Key Value Pair, what else is new...
        editor.putString("appoint", appointment);

        //4. Save your information.
        editor.apply();

        Toast.makeText(this, "Shared Preference Data Updated.", Toast.LENGTH_LONG).show();
    }

}