package com.example.studybuddy.Fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studybuddy.Adapter.MessageAdapter;
import com.example.studybuddy.Adapter.UserAdapter;
import com.example.studybuddy.MessagingActivity;
import com.example.studybuddy.Model.Chat;
import com.example.studybuddy.Model.ChatList;
import com.example.studybuddy.Model.Profile;
import com.example.studybuddy.Model.User;
import com.example.studybuddy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<Profile> mUsers;

    String myid;
    FirebaseFirestore db;

    private List<ChatList> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        myid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersList = new ArrayList<>();


        db.collection("ChatList").document(myid).collection("chat_with")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        usersList.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            ChatList chatlist = doc.toObject(ChatList.class);
                            usersList.add(chatlist);
                        }

                        chatList();
                    }
                });






        // Inflate the layout for this fragment
        return view;

    }



    private void chatList() {
        mUsers = new ArrayList<>();
        final CollectionReference colRef = db.collection("Profile");
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                mUsers.clear();

                for (QueryDocumentSnapshot doc : value) {
                    Profile profile = doc.toObject(Profile.class);
                    for(ChatList chatList : usersList){
                        if(profile.getUser_id().equals(chatList.getId())){
                            mUsers.add(profile);
                        }
                    }


                }
                userAdapter = new UserAdapter(getContext(), mUsers);
                recyclerView.setAdapter(userAdapter);
            }
        });


    }


}
