package com.example.studybuddy;

import java.util.List;

public class User {
    //User Object

    private String email;
    private String FirstName;
    private String LastName;
    private String gender;
    private String password;
    private int graduationYear;
    private List<Course> tutorCourse;
    private List<Course> studentCourse;
    private List<Appointment> appointments;
    private int TotalRating;



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


}
