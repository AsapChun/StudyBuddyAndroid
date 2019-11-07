package com.example.studybuddy;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

public class UserClassesFragment extends Fragment {

    private ScrollView scrollClasses;

    public UserClassesFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_user_classes, container, false);
    }

    public void OnStart(){ //similar to onCreate in mainactivity
        View fragmentView = getView();
        scrollClasses = (ScrollView) fragmentView.findViewById(R.id.scrClasses);






    }
}
