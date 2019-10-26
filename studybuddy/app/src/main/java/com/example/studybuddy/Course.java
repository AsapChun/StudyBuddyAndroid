package com.example.studybuddy;

import java.util.List;

public class Course {
    private String courseId;
    private List<User> tutors;
    private List<User> students;

    public Course(String id){
        courseId = id;
    }

    public void addTutors(User u){
        tutors.add(u);
    }

    public void addStudent(User u){
        students.add(u);
    }



}
