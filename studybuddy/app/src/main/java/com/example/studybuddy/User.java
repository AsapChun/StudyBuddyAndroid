package com.example.studybuddy;

import java.util.List;

public class User {
    //User Object

    public String userId;
    public String email;
    public String FirstName;
    public String LastName;
    public String gender;
    public String password;
    public int graduationYear;
    public List<Course> tutorCourse;
    public List<Course> studentCourse;
    public List<Appointment> appointments;
    public List<String> ratings;
    private float avgRating;
    public List<String> reviews;
    public String img_url;

    public User(){

    }


    public User(String e, String firstN, String lastN, String g, int year , String pass){
        email = e; //set email
        FirstName = firstN;
        LastName = lastN;
        gender = g;
        graduationYear = year;
        password = pass;

    }
    public String getEmail(){

        return email;
    }

    public String getName(){
        return FirstName + " " + LastName;
    }

    public String getGender(){
        return gender;
    }

    public void addTutor(Course c){
        tutorCourse.add(c);
    }
    public void addStudent(Course c){
        studentCourse.add(c);
    }
    public void addAppointment(Appointment a){
        appointments.add(a);
    }

    public float getAvgRating(){
        if(ratings == null || ratings.size() == 0)
            return 0;

        float sum = 0;
        for(String r : ratings)
            sum += Float.valueOf(r);
        return sum/ratings.size();
    }

}
