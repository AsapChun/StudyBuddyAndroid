package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.studybuddy.Adapter.MessageAdapter;
import com.example.studybuddy.Adapter.UserAdapter;
import com.example.studybuddy.Model.Chat;
import com.example.studybuddy.Model.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessagingActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser current_user;

    FirebaseFirestore db;

//    Toolbar toolbar;

    Intent intent;
    String TAG;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
//        toolbar = findViewById()
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessagingActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        db = FirebaseFirestore.getInstance();
        intent = getIntent();
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        //the user_id of the person you intend to chat with
        final String user_id = intent.getStringExtra("user_id");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(current_user.getUid(), user_id, msg);
                }else{
                    Toast.makeText(MessagingActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });



        final CollectionReference colRef = db.collection("Profile");
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }


                for (QueryDocumentSnapshot doc : value) {

                    Profile profile = doc.toObject(Profile.class);
                    Log.w(TAG, "user_id: " + user_id);
                    Log.w(TAG, "doc id " + profile.getUser_id());
                    if (user_id.equals(profile.getUser_id())) {
                        Log.w(TAG, "IDs are equal");
                        username.setText(profile.getFirst_name() + " " + profile.getLast_name());
                        if(profile.getImage_url().equals("")){
                            profile_image.setImageResource(R.mipmap.ic_launcher);
                        } else{
                            Glide.with(MessagingActivity.this).load(profile.getImage_url()).into(profile_image);
                        }
                        readMessages(current_user.getUid(), profile.getUser_id(), profile.getImage_url());
                    }

                }

            }
        });


    }

    private void sendMessage(String sender, String receiver, String message){

        final CollectionReference colRef = db.collection("Chats");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        colRef.add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }
    private void readMessages(final String myid, final String userid, final String imageurl){
        Log.w(TAG, "In read Messages");
        mchat = new ArrayList<>();
        final CollectionReference colRef = db.collection("Chats");
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                mchat.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Chat chat = doc.toObject(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid)||
                        chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        Log.w(TAG, "IDs are equal");
                        mchat.add(chat);
                        Log.w(TAG, "mchat: "+ mchat.toString());

                    }
                    messageAdapter = new MessageAdapter(MessagingActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);

                }
            }
        });




    }
}
