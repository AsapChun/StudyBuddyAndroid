package com.example.studybuddy;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String TAG;
//    private StorageReference dbRef;
    //path where image of user profile  and cover will be stored
    private String storagePath = "Users_Profile_Cover_Imgs/";


    //Views
    private TextView firstNameTv, lastNameTv, classYearTv, classTv, classTutorTv;
    private ImageView avatarIv, coverIv;
    private FloatingActionButton fab;
    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLEY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    public String first_name, last_name, class_year;
    public ArrayList<String> tutor_class, your_class, tutor_session;
    public ArrayList<CheckBox> cbArrayList;
    private static final ArrayList<String> courses = new ArrayList<String>( Arrays.asList("cs101", "cs103", "cs105", "cs111", "cs112", "cs131", "cs132", "cs210",
    "cs235", "cs237", "cs320", "cs330", "cs350", "cs391", "cs410", "cs411", "cs440", "cs460", "cs480", "cs542", "cs558", "cs640"));

    String cameraPermission[];
    String storagePermission[];

    //Progress Dialog
    ProgressDialog pd;

    //uri of picked image
    Uri image_uri;
    String profileOrCoverPhoto;

    StorageReference storageReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firstNameTv = findViewById(R.id.firstNameTv);
        lastNameTv =   findViewById(R.id.lastNameTv);
        classYearTv = findViewById(R.id.classYearTv);
        avatarIv = findViewById(R.id.avatarIv);
        coverIv = findViewById(R.id.coverIv);
        fab = findViewById(R.id.fab);
        pd = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        classTv = findViewById(R.id.classTv);
        classTutorTv = findViewById(R.id.classTutorTv);


        //init array of permissions
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

//
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                goBack();
//            }
//        });

        final DocumentReference docRef = db.collection("Profile").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Map<String,Object> data = document.getData();
                        if (data != null) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            first_name = data.get("first_name").toString();
                            last_name = data.get("last_name").toString();
                            class_year = data.get("class_year").toString();
                            tutor_class = (ArrayList<String>) data.get("tutor_class");
                            your_class = (ArrayList<String>) data.get("your_class");
                            tutor_session = (ArrayList<String>) data.get("tutor_session");

                            classTv.setText(toStringArrayList(your_class));
                            classTutorTv.setText(toStringArrayList(tutor_class));

                            firstNameTv.setText(first_name);
                            lastNameTv.setText(last_name);
                            classYearTv.setText(class_year);
                            try{

                                String image_url = data.get("image_url").toString();
                                Picasso.get().load(image_url).into(avatarIv);

                            }catch (Exception e){
//                                Picasso.get().load(R.drawable.ic_add_image).into(avatarIv);

                            }
                            try{

                                String coverImg_url = data.get("cover_url").toString();
                                Picasso.get().load(coverImg_url).into(coverIv);

                            }catch (Exception e){


                            }
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //fab Button Clicked
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });


    }

    private String toStringArrayList(ArrayList<String> lst){

        StringBuilder listString = new StringBuilder();

        for (int i = 0; i <lst.size();i++)
        {
            if(i == lst.size()-1) {
                listString.append(lst.get(i));
                return listString.toString();
            }

            listString.append(lst.get(i) + ", ");
        }

        return listString.toString();
    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
        String options[] = {"Edit Profile Picture", "Edit Cover Photo", "Edit General Profile", "Edit Class Tutor", "Edit My Class"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set Title
        builder.setTitle("Choose Action");
        //set item dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    //Edit Profile Clicked
                    pd.setMessage("Updating Profile Picture");
                    profileOrCoverPhoto = "image_url";
                    showImagePicDialog();
                }
                else if(which ==1){
                    //Edit Cover Clicked
                    pd.setMessage("Updating Cover Picture");
                    profileOrCoverPhoto = "cover_url";
                    showImagePicDialog();
                }

                else if(which==2){
                    //Edit First Name Clicked
                    pd.setMessage("Updating General Profile");
                    showNameUpdateDialog("General Profile");
                }
                else if(which==3){
                    //Edit First Name Clicked
                    pd.setMessage("Updating Your Tutor Class");
                    showClassUpdateDialog("Tutor Class");
                }
                else if(which==4){
                    //Edit First Name Clicked
                    pd.setMessage("Updating Your Class");
                    showClassUpdateDialog("Class");
                }


            }
        });
        builder.create().show();
    }

    private void showClassUpdateDialog(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update "+ key);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        ScrollView sv = new ScrollView(this);


        cbArrayList = new ArrayList<CheckBox>();

        for(int i = 0; i < courses.size(); i++) {
            CheckBox cb = new CheckBox(this);
            cb.setText(courses.get(i));
            cb.setId(i+courses.size());
            cb.setTextSize(1,20);
            cbArrayList.add(i, cb);

            linearLayout.addView(cb);
        }
        if(key.equals("Tutor Class")){
            for (int i = 0; i < tutor_class.size(); i++) {
                String the_class = tutor_class.get(i);
                int the_index = findCheckBoxIndex(the_class);
                if(the_index != Integer.MIN_VALUE) {
                    CheckBox cb = cbArrayList.get(the_index);
                    cb.setChecked(true);
                }
            }
        }else{
            for (int i = 0; i < your_class.size(); i++) {
                String the_class = your_class.get(i);
                int the_index = findCheckBoxIndex(the_class);
                if(the_index != Integer.MIN_VALUE) {
                    CheckBox cb = cbArrayList.get(the_index);
                    cb.setChecked(true);
                }

            }

        }
        sv.addView(linearLayout);
        builder.setView(sv);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                pd.show();
                ArrayList<String> newArrayList = new ArrayList<String>();
                for (int i = 0; i < cbArrayList.size(); i++) {
                    CheckBox cb = cbArrayList.get(i);
                    if(cb.isChecked()) {
                        String the_class = cb.getText().toString();
                        newArrayList.add(the_class);


                    }

                }
                if(key.equals("Tutor Class")){
                    updateClass("tutor_class", newArrayList);
                }
                else{
                    updateClass("your_class", newArrayList);
                }

                


            }
        });

        builder.create().show();
    }

    private void updateClass(String field, ArrayList courses) {
        DocumentReference ProfileRef = db.collection("Profile").document(mAuth.getCurrentUser().getUid());
        ProfileRef
                .update(field, courses)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Updated...", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Error Updating ...", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private int findCheckBoxIndex(String the_class) {
        System.out.println(the_class);
        for(int i = 0; i<cbArrayList.size();i++){

            String the_text = cbArrayList.get(i).getText().toString();
            if(the_text.equals(the_class)){
                return i;
            }
        }
            return Integer.MIN_VALUE;
    }


    private void showNameUpdateDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update "+key);
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add text

        // First Name
        final EditText editText = new EditText(this);
        editText.setHint("Enter " + "First Name");
        editText.setText(first_name);
        linearLayout.addView(editText);

        //LastName
        final EditText editText1 = new EditText(this);
        editText1.setHint("Enter " + "Last Name");
        editText1.setText(last_name);
        linearLayout.addView(editText1);

        //Class Year
        final EditText editText2 = new EditText(this);
        editText2.setHint("Enter " + "Class Year");
        editText2.setText(class_year);
        linearLayout.addView(editText2);
        builder.setView(linearLayout);

        //add button in dialog to update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();
                String value1 = editText1.getText().toString().trim();
                String value2 = editText2.getText().toString().trim();
                //validate if user has entered something or not
                if(!TextUtils.isEmpty(value) && !TextUtils.isEmpty(value1)){
                    pd.show();
                    Map<String, Object> result = new HashMap<>();
                    result.put("first_name", value);
                    result.put("last_name", value1);
                    result.put("class_year", value2);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = mAuth.getCurrentUser().getUid();

                        db.collection("Profile").document(userId).update(result)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "Updated...", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(getIntent());

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "Error Updating ...", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }



                }
                else{
                    Toast.makeText(getApplicationContext(), "Enter first and last name" , Toast.LENGTH_SHORT).show();
                }

            }
        });
        //add button in dialog to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void showImagePicDialog() {
        //show dialog containing options Camera and Gallery to Pick the Image
        String options[] = {"Camera", "Gallery"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set Title
        builder.setTitle("Pick Image From");
        //set item dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    //Camera Clicked
                    if(!checkCameraPermission()) {
                        requestCameraPermission();
                    }
                    else{
                        pickFromCamera();
                    }

                }
                else if(which ==1){
                    //Gallery Clicked
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }

                }


            }
        });
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                //picking from camera, first check if camera and storage permission allowed or not
                if (grantResults.length>0){
                    boolean cameraAccepted =  grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted =  grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        //permission enabled
                        pickFromCamera();
                    }else{
                        //permission denied
                        Toast.makeText(getApplicationContext(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();

                    }

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                //picking from gallery, first check if storage permission allowed or not
                if (grantResults.length>0){

                    boolean writeStorageAccepted =  grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        //permission enabled
                        pickFromGallery();
                    }else{
                        //permission denied
                        Toast.makeText(getApplicationContext(), "Please enable storage permission", Toast.LENGTH_SHORT).show();

                    }

                }

            }
            break;
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //method will be called after user pick image from camera or gallery

        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLEY_CODE){
                //image is picked from gallery, get uri of image
                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of image
                uploadProfileCoverPhoto(image_uri);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        //show progress
        pd.show();
        //Instead of Creating a separate function for Profile Picture and Cover Photo
        // We do work for the same function
        String filePathAndName = storagePath + "" + profileOrCoverPhoto + "_" + mAuth.getCurrentUser().getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                //check if image is uploaded or not and url is received
                if(uriTask.isSuccessful()){

                    HashMap<String, Object> results = new HashMap<>();

                    results.put(profileOrCoverPhoto, downloadUri.toString());
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = mAuth.getCurrentUser().getUid();


                        db.collection("Profile").document(userId).update(results)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "Image Updated...", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(getIntent());

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "Error Updating Image...", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void pickFromCamera() {
        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //put image uri
        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //Pick from Gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLEY_CODE);
    }



}
