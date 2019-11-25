package com.example.studybuddy;

import com.example.studybuddy.Model.Profile;

import java.util.List;

public class Course {
    private String courseId;
    private List<Profile> tutors;
    private List<Profile> students;

    public Course(String id){
        courseId = id;
    }

    public void addTutors(Profile u){
        tutors.add(u);
    }

    public void addStudent(Profile u){
        students.add(u);
    }



}
